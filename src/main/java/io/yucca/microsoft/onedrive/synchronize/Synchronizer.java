/**
 * Copyright 2015 Rob Sessink
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.yucca.microsoft.onedrive.synchronize;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ConfigurationUtil;
import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveConfiguration;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.OneDriveFolder;
import io.yucca.microsoft.onedrive.OneDriveImpl;
import io.yucca.microsoft.onedrive.SyncResponse;
import io.yucca.microsoft.onedrive.actions.CreateAction;
import io.yucca.microsoft.onedrive.actions.DeleteAction;
import io.yucca.microsoft.onedrive.actions.ResyncNeededException;
import io.yucca.microsoft.onedrive.actions.SyncAction;
import io.yucca.microsoft.onedrive.actions.UpdateAction;
import io.yucca.microsoft.onedrive.actions.UploadAction;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Synchronizes a complete OneDrive with a LocalDrive or a specific folder with
 * the local folder and vise versa.
 * 
 * @author yucca.io
 */
public class Synchronizer {

    private static final Logger LOG = LoggerFactory
        .getLogger(Synchronizer.class);

    private final LocalDrive localDrive;

    private final OneDrive oneDrive;

    private final OneDriveAPIConnection api;

    private final OneDriveConfiguration configuration;

    private final LocalDriveSynchronizer repository;

    /**
     * Constructs a Synchronizer to synchronize a complete OneDrive.
     * 
     * @param synchronizer LocalDriveSynchronizer for the local drive
     * @param api OneDriveAPIConnection connection used for synchronization
     * @param configuration OneDriveConfiguration
     * @throws IOException
     */
    public Synchronizer(LocalDriveSynchronizer synchronizer,
                        OneDriveAPIConnection api,
                        OneDriveConfiguration configuration)
                            throws IOException {
        this.repository = synchronizer;
        this.localDrive = repository.getLocalDrive();
        this.oneDrive = OneDriveImpl.defaultDrive(api);
        this.api = api;
        this.configuration = configuration;
    }

    /**
     * Synchronize the complete OneDrive with the LocalDrive
     * 
     * @param deltaSynchronization boolean false for a full synchronization
     * @throws IOException
     */
    public void synchronize(boolean deltaSynchronization) throws IOException {
        synchronize(localDrive.getPath(), oneDrive.getAddress(),
                    deltaSynchronization);
    }

    /**
     * Synchronize a specific folder in OneDrive with the LocalDrive
     * 
     * @param path Path path to LocalDrive
     * @param folderAddress ItemAddress of remote folder
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            false for a full synchronization
     * @throws IOException
     */
    void synchronize(Path path, ItemAddress folderAddress,
                     boolean deltaSynchronization) throws IOException {
        initialiseLocalFolder(path, folderAddress);
        LocalFolder folder = new LocalFolderImpl(path, repository);
        synchronizeFolder(folder, folderAddress, deltaSynchronization);
    }

    private LocalFolder initialiseLocalFolder(Path path,
                                              ItemAddress folderAddress)
                                                  throws IOException {
        OneDriveFolder remoteFolder = oneDrive.getFolder(folderAddress);
        LocalFolderImpl folder = new LocalFolderImpl(path, remoteFolder,
                                                     repository);
        folder.create();
        return folder;
    }

    /**
     * Synchronize OneDrive with LocalDrive and vise versa. After
     * synchronization the deltaToken is saved in the configuration for future
     * delta synchronizations.
     * 
     * @param folder LocalFolder local folder to synchronize
     * @param folderAddress ItemAddress of remote folder
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            false for a full synchronization
     * @throws IOException
     * @throws ConfigurationException if deltaToken cannot be saved to
     *             configuration file
     */
    public void synchronizeFolder(LocalFolder folder, ItemAddress folderAddress,
                                  boolean deltaSynchronization)
                                      throws IOException {
        String deltaToken = getDeltaToken(deltaSynchronization);
        deltaSynchronization = initializeSession(deltaSynchronization,
                                                 deltaToken, folder);
        try {
            SyncResponse syncResponse = getChangesForFolder(folderAddress,
                                                            deltaToken);
            synchronizeChangesBothWays(syncResponse, deltaSynchronization);
        } catch (ResyncNeededException e) {
            LOG.info("Resynchronisation of folder: {} is needed, starting a fresh enumeration.",
                     folderAddress);
            resynchronizeChanges(e);
        }
    }

    /**
     * Get the changes for a folder
     * 
     * @param folderAddress ItemAddress of remote folder
     * @param deltaToken String previous state, {@code null} for a full
     *            enumeration
     * @return SyncResponse enumerated changed
     * @throws ResyncNeededException if deltaToken is expired
     */
    private SyncResponse getChangesForFolder(ItemAddress folderAddress,
                                             String deltaToken)
                                                 throws ResyncNeededException {
        SyncAction action = new SyncAction(api, folderAddress, deltaToken,
                                           null);
        return action.call();
    }

    /**
     * Read the delta token from configuration
     * 
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            false for a full synchronization
     * @return String deltaToken previous state, {@code null} for a full
     *         enumeration
     */
    private String getDeltaToken(boolean deltaSynchronization) {
        String deltaToken = null;
        if (deltaSynchronization) {
            LOG.info("Loading delta token from configuration: {}",
                     configuration.getConfigurationFile());
            deltaToken = configuration.getDeltaToken();
        }
        return deltaToken;
    }

    /**
     * Initialize the synchronization session.
     * <p>
     * If the synchronization type is delta and a deltaToken exists, a saved
     * session forms the basis for this synchronization. Otherwise a clean
     * session is initialized and a full synchronization must be performed.
     * </p>
     * 
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            false for a full synchronization
     * @param deltaToken String previous state, {@code null} for a full
     *            enumeration
     * @param folder LocalFolder folder to synchronize
     * @return boolean true if a delta synchronization can be performed, false
     *         if a full synchronization must be performed
     */
    private boolean initializeSession(boolean deltaSynchronization,
                                      String deltaToken, LocalFolder folder) {

        if (deltaSynchronization && !hasDeltaToken(deltaToken)) {
            LOG.warn("A delta synchronization is requested, but no deltaToken is available, performing an full synchronization instead.");
            repository.initializeSession(false, folder);
            return false;
        } else if (deltaSynchronization) {
            repository.initializeSession(true, folder);
            return true;
        } else {
            repository.initializeSession(false, folder);
            return false;
        }
    }

    private boolean hasDeltaToken(String deltaToken) {
        return !(deltaToken == null || "".equals(deltaToken));
    }

    private void saveSession() {
        repository.saveSession();
    }

    /**
     * Synchronize the OneDrive with the LocalDrive. This uses the understanding
     * workflow:
     * 
     * <pre>
     * 1. On delta synchronization, compare the current state of the 
     * local drive with the saved state. Items that only exist in the saved 
     * states indicate a deletion and are therefor removed from OneDrive. If 
     * the deleted item is on the change list, then remove it from this list 
     * to prevent a recreation.
     * 2. Items registered for addition (not having an id) are created in OneDrive
     * and added to items list for an up-to-date drive state
     * 3. Process the (delta) changes acquired from OneDrive and reflect these change
     * to the local drive and vise-versa
     * 4. Save/serialize the local drive state to disk
     * 5. Save the token for a future enumeration (XXX should be done per drive/folder/item)
     * </pre>
     * 
     * @param response SyncResponse enumerated changes
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            false for a full synchronization
     * @throws OneDriveException if the synchronization process fails, if errors
     *             occur on processing individual files or folders then these
     *             are skipped
     */
    private void synchronizeChangesBothWays(SyncResponse response,
                                            boolean deltaSynchronization) {
        try {
            LOG.info("Started a {} two-way synchronization of OneDrive: {} and LocalDrive: {}",
                     syncMethod(deltaSynchronization), oneDrive, localDrive);
            Map<String, Item> deltaMap = response.asMap();
            processLocalDeletions(deltaSynchronization, deltaMap);
            processLocalAdditions();
            processChanges(deltaMap);
            saveSession();
            saveDeltaToken(response.getToken());
            LOG.info("Succesfully synchronized OneDrive: {} and LocalDrive: {} two-ways",
                     oneDrive, localDrive);
        } finally {
            repository.clearSession();
        }
    }

    /**
     * Resynchronize changes after a deltaToken was found to be expired
     * 
     * @param exception ResyncNeededException
     */
    private void resynchronizeChanges(ResyncNeededException exception) {
        SyncResponse syncResponse = SyncAction.byURI(api,
                                                     exception.getNextLink());
        switch (exception.getDetailedErrorCode()) {
        case RSYNCHAPPLYDIFFERENCES:
            resynchronizeChangesApplyDifferences(syncResponse);
            break;
        case RSYNCHUPLOADDIFFERENCES:
            resynchronizeChangesUploadDifferences(syncResponse);
            break;
        default:
            throw new OneDriveException("Unknown resynchronization error.");
        }
    }

    private void resynchronizeChangesApplyDifferences(SyncResponse response) {
        LOG.info("Resynchronizing changes and apply differences on OneDrive: {} and LocalDrive: {}",
                 oneDrive, localDrive);
        Map<String, Item> deltaMap = response.asMap();
        processLocalDeletions(true, deltaMap); // must check if server
                                               // version exists
        processLocalAdditions(); // must check if server version exists
        processChanges(deltaMap);
        saveSession();
        saveDeltaToken(response.getToken());
        LOG.info("Succesfully resynchronized and applied changes for OneDrive: {} and LocalDrive: {}",
                 oneDrive, localDrive);
    }

    private void resynchronizeChangesUploadDifferences(SyncResponse response) {
        LOG.info("Resynchronizing changes and upload differences on OneDrive: {} and LocalDrive: {}",
                 oneDrive, localDrive);
        Map<String, Item> deltaMap = response.asMap();
        processChanges(deltaMap); // keep both copies if you're not sure which
                                  // one is more up-to-date?
        saveSession();
        saveDeltaToken(response.getToken());
        LOG.info("Succesfully resynchronized and uploaded differences for OneDrive: {} and LocalDrive: {}",
                 oneDrive, localDrive);
    }

    /**
     * Process the changes acquired from the OneDrive API to the LocalDrive and
     * process localy changed files or folders to OneDrive.
     * 
     * @param deltaMap Map<String, Item> delta changes acquired from OneDrive
     */
    private void processChanges(Map<String, Item> deltaMap) {
        LOG.info("Processing enumerated changes from OneDrive: {} with LocalDrive: {}",
                 oneDrive, localDrive);
        Iterator<Item> it = deltaMap.values().iterator();
        while (it.hasNext()) {
            Item updated = it.next();
            try {
                LocalItem local = repository.getLocalItem(updated.getId());
                if (local != null) {
                    LOG.info("Item: {}, id: {} is on the delta list will be synchronized",
                             local.getPath(), local.getId());
                    if (updated.isDeleted()) {
                        deleteLocaly(local, updated);
                        break;
                    }
                    switch (local.lastModificationStatus(updated)) {
                    case NOTMODIFIED:
                        break;
                    case NEWER:
                        updateOneDrive(local, updated);
                        break;
                    case OLDER:
                        updateLocaly(local, updated);
                        break;
                    }
                } else {
                    addLocaly(updated);
                }
            } catch (IOException | OneDriveException e) {
                LOG.error("Failure processing item: {}, name: {}, skipped!",
                          updated.getId(), updated.getName(), e);
            } finally {
                it.remove();
            }
        }
    }

    /**
     * Delete localy deleted files also OneDrive. Localy deleted files are
     * determined by comparing the current state of the LocalDrive with the
     * saved state. If an historical item is not available in the current state
     * then we assume it is deleted localy and therefor also removed in
     * OneDrive.
     * 
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            false for a full synchronization
     * @param deltaMap Map<String, Item> delta changes acquired from OneDrive
     */
    private void processLocalDeletions(boolean deltaSynchronization,
                                       Map<String, Item> deltaMap) {
        if (!deltaSynchronization) {
            return;
        }
        LOG.info("Processing deletions in LocalDrive: {} with OneDrive: {}",
                 localDrive, oneDrive);
        for (LocalItem local : repository.getDeletions()) {
            try {
                LOG.info("Item: {}, id: {}, was deleted localy, deleting item from OneDrive",
                         local.getPath(), local.getId());
                new DeleteAction(api, new IdAddress(local.getId())).call();
                // remove the deleted item from delta list to prevent a possible
                // recreation of the item
                deltaMap.remove(local.getId());
                LOG.info("Deleted item: {}, id: {} from OneDrive",
                         local.getPath(), local.getId());
            } catch (OneDriveException e) {
                LOG.error("Failure deleting OneDrive item triggered by local deletion of item: {}, path: {}, skipped!",
                          local.getId(), local.getPath(), e);
            }
        }
    }

    /**
     * Handle localy created files or folders, upload or create these in
     * OneDrive
     */
    private void processLocalAdditions() {
        LOG.info("Processing additions in LocalDrive: {} with OneDrive: {}",
                 localDrive, oneDrive);
        for (LocalItem local : repository.getAdditions()) {
            try {
                Item uploaded = null;
                LocalResource parent = repository
                    .getLocalFolder(local.getParentId());
                LOG.info("Item: {} was added localy, adding item to OneDrive",
                         local.getPath());
                if (ResourceType.FILE.equals(local.type())) {
                    ItemAddress parentAddress = new IdAddress(parent.getId());
                    UploadAction action = new UploadAction(api,
                                                           ((LocalFile)local)
                                                               .getContent(),
                                                           parentAddress,
                                                           ConflictBehavior.FAIL);
                    uploaded = action.call();
                } else {
                    ItemAddress parentAddress = new IdAddress(parent.getId());
                    CreateAction action = new CreateAction(api, local
                        .getName(), parentAddress, ConflictBehavior.FAIL);
                    uploaded = action.call();
                }
                local.update(uploaded);
                LOG.info("Added item: {} to OneDrive under id: {}",
                         local.getPath(), local.getId());
            } catch (IOException | OneDriveException e) {
                LOG.error("Failure creating OneDrive item triggered by local addition of item: {}, path: {}, skipped!",
                          local.getId(), local.getPath(), e);
            }
        }
    }

    /**
     * Update/create a local item in OneDrive
     * 
     * @param local LocalItem in local drive
     * @param item Item related item in OneDrive
     */
    private void updateOneDrive(LocalItem local, Item item) {
        try {
            LOG.info("Item: {}, id: {} was modified localy, modifying item in OneDrive",
                     local.getPath(), local.getId());
            if (ResourceType.FILE.equals(local.type())
                && local.isContentModified(item)) {
                ItemAddress parentAddress = new IdAddress(local.getParentId());
                UploadAction action = new UploadAction(api, ((LocalFile)local)
                    .getContent(), parentAddress, ConflictBehavior.REPLACE);
                action.call();
            } else if (repository.isLocalDriveRoot(local)) {
                // updating of OneDrive root folder is prohibited by the API
                return;
            }
            local.updateItem(item);
            new UpdateAction(api, item).call();
            LOG.info("Updated item: {}, id: {} in OneDrive", local.getPath(),
                     local.getId());
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure updating OneDrive item: {} trigger by local modification of item: {}, id: {}, skipped!",
                      new Object[] { item.getId(), local.getPath(),
                                     local.getId() },
                      e);
        }
    }

    /**
     * If an item does not exist localy, is on the delta list and is not deleted
     * add it localy
     * 
     * @param updated Item
     */
    private void addLocaly(Item updated) {
        try {
            if (updated.isDeleted()) {
                return;
            }
            LOG.info("Item: {}, id: {} was added in OneDrive, adding item to LocalDrive",
                     updated.getName(), updated.getId());
            LocalItem local = LocalResourceFactory.newInstance(updated, api,
                                                               repository);
            local.update(updated);
            LOG.info("Added item: {}, id: {} to LocalDrive", local.getPath(),
                     local.getId());
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure adding local item trigger by OneDrive addition of item: {}, id: {}, skipped!",
                      updated.getName(), updated.getId(), e);
        }
    }

    private void updateLocaly(LocalItem local, Item updated) {
        try {
            LOG.info("Item: {}, id: {} was modified in OneDrive, modifying item in LocalDrive",
                     updated.getName(), updated.getId());
            if (ResourceType.FILE.equals(local.type())
                && local.isContentModified(updated)) {
                local = LocalResourceFactory.newInstance(updated, api,
                                                         repository);
            }
            local.update(updated);
            LOG.info("Updated item: {}, id: {} to LocalDrive", local.getPath(),
                     local.getId());
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure updating local item: {} from OneDrive modification of item: {}, id: {}, skipped!",
                      new Object[] { local.getPath(), updated.getName(),
                                     updated.getId() },
                      e);
        }
    }

    private void deleteLocaly(LocalItem local, Item updated)
        throws IOException {
        try {
            LOG.info("Item: {}, id: {} was deleted in OneDrive, deleting item in LocalDrive",
                     updated.getName(), updated.getId());
            local.delete();
            LOG.info("Deleted item: {}, id: {} in LocalDrive", local.getPath(),
                     local.getId());
        } catch (IOException e) {
            LOG.error("Failure deleting local item: {} triggered by OneDrive deletion of item: {}, id: {}, skipped!",
                      new Object[] { local.getPath(), updated.getName(),
                                     updated.getId(), },
                      e);
        }
    }

    private void saveDeltaToken(String deltaToken) {
        try {
            LOG.info("Saving delta token in configuration: {}",
                     configuration.getConfigurationFile());
            configuration.setDeltaToken(deltaToken);
            ConfigurationUtil.save(configuration);
        } catch (ConfigurationException e) {
            throw new OneDriveException("Failure saving deltaToken", e);
        }
    }

    /**
     * Get synchronization method
     * 
     * @param deltaSynchronization boolean
     * @return String
     */
    private String syncMethod(boolean deltaSynchronization) {
        return deltaSynchronization ? "delta" : "full";
    }

}
