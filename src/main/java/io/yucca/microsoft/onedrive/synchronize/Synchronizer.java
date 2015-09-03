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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.ConfigurationUtil;
import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveConfiguration;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.OneDriveFolder;
import io.yucca.microsoft.onedrive.OneDriveItem;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SyncResponse;

/**
 * OneDriveFolderSynchronizer, synchronizes the complete OneDrive with the
 * LocalDrive or a specific folder with the local folder and vise versa.
 * 
 * @author yucca.io
 */
public class Synchronizer {

    public static final String FILE_LOCAL_DRIVE_STATE = ".onedrivestate_";

    private final Logger LOG = LoggerFactory.getLogger(Synchronizer.class);

    private final LocalDrive localDrive;

    private final OneDrive remoteDrive;

    private final OneDriveItem remoteFolder;

    private final LocalFolder localFolder;

    private final OneDriveAPIConnection api;

    private final OneDriveConfiguration configuration;

    /**
     * Map with items that were localy added and must be created in OneDrive
     */
    private final List<LocalItem> additions = new LinkedList<>();

    /**
     * Map with synchronized items in the LocalDrive which are possibly changed
     */
    private final Map<String, LocalItem> items = new LinkedHashMap<>();

    /**
     * Map with local synchronized folders, used in the processing of items to
     * acquire the parent folder
     */
    private final Map<String, LocalResource> folders = new HashMap<>();

    /**
     * Map holding the previous state of the LocalDrive, used to determine
     * localy deleted items
     */
    private List<LocalItem> savedState;

    /**
     * Constructs a Synchronizer to sychronize a complete OneDrive.
     * 
     * @param localDrive LocalDrive local drive
     * @param remoteDrive OneDrive remote drive
     * @param api OneDriveAPIConnection connection used for synchronization
     * @param configuration OneDriveConfiguration
     * @throws IOException
     */
    public Synchronizer(LocalDrive localDrive, OneDrive remoteDrive,
                        OneDriveAPIConnection api,
                        OneDriveConfiguration configuration)
                            throws IOException {
        this.localDrive = localDrive;
        this.localFolder = new LocalFolder(localDrive.getPath());
        this.remoteDrive = remoteDrive;
        this.remoteFolder = remoteDrive.getRootFolder();
        this.api = api;
        this.configuration = configuration;
    }

    /**
     * Constructs a Synchronizer to sychronize a specific folder within a
     * OneDrive.
     * 
     * @param localDrive local drive
     * @param localFolder local folder to synchronize
     * @param remoteDrive remote drive
     * @param remoteFolder remote folder to synchronize
     * @param api connection used for synchronization
     */
    public Synchronizer(LocalDrive localDrive, LocalFolder localFolder,
                        OneDrive remoteDrive, OneDriveFolder remoteFolder,
                        OneDriveAPIConnection api,
                        OneDriveConfiguration configuration) {
        this.localDrive = localDrive;
        this.localFolder = localFolder;
        this.remoteDrive = remoteDrive;
        this.remoteFolder = remoteFolder;
        this.api = api;
        this.configuration = configuration;
    }

    /**
     * Synchronize OneDrive with LocalDrive and vise versa. After
     * synchronization the deltaToken is saved in the configuration for future
     * delta synchronizations.
     * 
     * @param deltaSynchronization boolean true for deltaSynchronization and
     *            true for a full synchronization
     * @throws ConfigurationException if deltaToken cannot be saved to
     *             configuration file
     */
    public void synchronize(boolean deltaSynchronization) {
        String deltaToken = getDeltaToken(deltaSynchronization);
        readLocalDriveState(deltaSynchronization, deltaToken);
        SyncResponse syncResponse = api
            .syncChangesById(remoteFolder.getItemId(), deltaToken);
        walkTree(syncResponse.asMap(), (deltaToken != null));
        deltaToken = syncResponse.getToken();
        saveDeltaToken(deltaToken);
    }

    /**
     * On deltaSynchronization read the delta token from configuration
     * 
     * @param deltaSynchronization boolean
     * @return String deltaToken {@code null} for a full synchronization
     */
    private String getDeltaToken(boolean deltaSynchronization) {
        String deltaToken = null;
        if (deltaSynchronization) {
            deltaToken = configuration.getDeltaToken();
        }
        return deltaToken;
    }

    private boolean hasDeltaToken(String deltaToken) {
        return !(deltaToken == null || "".equals(deltaToken));
    }

    /**
     * Walk the LocalDrive and synchronize this with OneDrive. This used the
     * understanding workflow:
     * 
     * <pre>
     * 0. Initialize the root folder if this does not exist.
     * 1. Iterate all files and folders in the local drive.
     * 2. On delta synchronization, compare the current state of the 
     * local drive with the saved state. Items that only exist in the saved 
     * states indicate a deletion and are therefor removed from OneDrive. If 
     * the deleted item is on the change list, then remove it from this list 
     * to prevent a recreation.
     * 3. Items registered for addition (not having an id) are created in OneDrive
     * and added to items list for an up-to-date drive state
     * 4. Process the (delta) changes acquired from OneDrive and reflect these change
     * to the local drive and vise-versa
     * 5. Save/serialize the local drive state to disk
     * </pre>
     * 
     * @param deltaMap Map<String, Item> changes acquired from OneDrive
     * @param deltaSynchronization boolean false for full synchronization
     * @throws OneDriveException if the synchronization process fails, if errors
     *             occur on processing individual files or folders then these
     *             are skipped
     */
    private void walkTree(Map<String, Item> deltaMap,
                          boolean deltaSynchronization) {
        try {
            initializeRootFolder();
            LocalFileVisitor visitor = new LocalFileVisitor(this);
            Files.walkFileTree(localFolder.getPath(),
                               new HashSet<FileVisitOption>(),
                               Integer.MAX_VALUE, visitor);
            processLocalDeletions(deltaSynchronization, deltaMap);
            processLocalAdditions();
            processChanges(deltaMap);
            writeLocalDriveState();
        } catch (IOException e) {
            throw new OneDriveException("Failure synchronizing OneDrive: "
                                        + remoteDrive + " with LocalDrive: "
                                        + localDrive, e);
        } finally {
            clearMaps();
        }
    }

    private void clearMaps() {
        additions.clear();
        items.clear();
        folders.clear();
    }

    private void initializeRootFolder() throws IOException {
        registerFolder(localDrive);
    }

    /**
     * Process modification for local files or folders if these are on the delta
     * list acquired from OneDrive API and further process localy files or
     * folder to OneDrive.
     * 
     * @param deltaMap Map<String, Item> delta changes acquired from OneDrive
     */
    private void processChanges(Map<String, Item> deltaMap) {
        Iterator<Item> it = deltaMap.values().iterator();
        while (it.hasNext()) {
            Item updated = it.next();
            try {
                LocalItem local = items.get(updated.getId());
                if (local != null) {
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
                    LOG.debug("Item: {}, id: {} was on the delta list and is synchronized",
                              local.getPath(), local.getId());
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
     * @param deltaSynchronization
     */
    private void processLocalDeletions(boolean deltaSynchronization,
                                       Map<String, Item> deltaMap) {
        if (deltaSynchronization == false) {
            return;
        }
        for (LocalItem local : savedState) {
            try {
                if (items.containsKey(local.getId()) == false) {
                    api.deleteById(local.getId());
                    // remove deleted item from delta list to prevent a possible
                    // re-creation of the item
                    deltaMap.remove(local.getId());
                    LOG.debug("Item: {}, name: {} was deleted localy and deleted from OneDrive",
                              local.getId(), local.getName());
                }
            } catch (OneDriveException e) {
                LOG.error("Failure deleting OneDrive item triggered by local deletion of item: {}, name: {}, skipped!",
                          local.getId(), local.getName(), e);
            }
        }
    }

    /**
     * Handle localy created files or folders, upload or create these in
     * OneDrive
     */
    private void processLocalAdditions() {
        for (LocalItem local : additions) {
            try {
                Item uploaded = null;
                LocalResource parent = getParentResource(local.getParentId());
                if (ResourceType.FILE.equals(local.type())) {
                    uploaded = api.uploadByParentId(
                                                    ((LocalFile)local)
                                                        .getOneDriveContent(),
                                                    parent.getId(),
                                                    ConflictBehavior.FAIL);
                } else {
                    uploaded = api.createFolderById(local.getName(),
                                                    parent.getId(),
                                                    ConflictBehavior.FAIL);
                }
                local.update(uploaded, null, parent);
                registerItem(local);
                LOG.debug("Item: {} was added localy and is added to OneDrive under id: {}",
                          local.getPath(), local.getId());
            } catch (IOException | OneDriveException e) {
                LOG.error("Failure creating OneDrive item triggered by local addition of item: {}, name: {}, skipped!",
                          local.getId(), local.getPath(), e);
            }
        }
    }

    private void updateOneDrive(LocalItem local, Item item) {
        try {
            if (ResourceType.FILE.equals(local.type())
                && local.isContentModified(item)) {
                api.uploadByParentId(((LocalFile)local).getOneDriveContent(),
                                     local.getParentId(),
                                     ConflictBehavior.REPLACE);
            } else if (isLocalRoot(local)) {
                // updating of OneDrive root folder is prohibited by the API
                return;
            }
            local.updateItem(item);
            api.update(item, null);
            LOG.debug("Item: {}, id: {} was modified localy and is modified in OneDrive",
                      local.getPath(), local.getId());
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure updating OneDrive item: {} trigger by local modification of item: {}, name: {}, skipped!",
                      new Object[] { item.getId(), local.getId(),
                                     local.getPath() },
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
            // prevent creating of deleted items
            if (updated.isDeleted()) {
                return;
            }
            OneDriveContent content = null;
            if (updated.isFile()) {
                content = api.downloadById(updated.getId(), null);
            }
            LocalResource parent = getParentResource(updated
                .getParentReference().getId());
            LocalItem local = LocalResourceFactory.build(updated, content,
                                                         parent);
            if (ResourceType.FOLDER.equals(local.type())) {
                registerFolder(local);
            }
            // registered localy created item, otherwise it is not in
            // the saved state of the LocalDrive
            registerItem(local);
            LOG.debug("Added local item: {}, id: {}", local.getPath(),
                      local.getId());
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure adding local item trigger by OneDrive addition of item: {}, name: {}, skipped!",
                      updated.getId(), updated.getName(), e);
        }
    }

    private void updateLocaly(LocalItem local, Item updated) {
        try {
            OneDriveContent content = null;
            if (ResourceType.FILE.equals(local.type())
                && local.isContentModified(updated)) {
                content = api.downloadById(updated.getId(), null);
            }
            LocalResource parent = getParentResource(local.getParentId());
            local.update(updated, content, parent);
            LOG.debug("Updated local item: {}, id: {}", local.getPath(),
                      local.getId());
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure updating Local item: {} from OneDrive modification of item: {}, name: {}, skipped!",
                      new Object[] { local.getPath(), updated.getId(),
                                     updated.getName() },
                      e);
        }
    }

    private void deleteLocaly(LocalItem local, Item updated)
        throws IOException {
        try {
            local.delete();
            deregisterItem(local);
            LOG.debug("Deleted local item: {}, id: {}", local.getPath(),
                      local.getId());
        } catch (IOException e) {
            LOG.error("Failure deleting local item: {} triggered by OneDrive deletion of item: {}, name: {}, skipped!",
                      new Object[] { local.getPath(), updated.getId(),
                                     updated.getName() },
                      e);
        }
    }

    /**
     * Get Parent resource identified by Id
     * 
     * @param id String
     * @return LocalResource
     * @throws OneDriveException if parent is not registered as folder
     */
    public LocalResource getParentResource(String id) {
        LocalResource parent = folders.get(id);
        if (parent == null) {
            throw new OneDriveException("Parent folder with id: " + id
                                        + " does not exist in LocalDrive.");
        }
        return parent;
    }

    private void saveDeltaToken(String deltaToken) {
        try {
            configuration.setDeltaToken(deltaToken);
            ConfigurationUtil.save(configuration);
        } catch (ConfigurationException e) {
            throw new OneDriveException("Failure saving deltaToken", e);
        }
    }

    /**
     * Deserialize the local drive state on delta synchronization, or
     * instantiate a new state Map if no deltaToken or no local drive state is
     * available
     *
     * @param boolean deltaSynchronization
     * @param String deltaToken
     * @throws OneDriveException of state cannot be deserialized
     */
    @SuppressWarnings("unchecked")
    private boolean readLocalDriveState(boolean deltaSynchronization,
                                        String deltaToken) {
        if ((deltaSynchronization && hasDeltaToken(deltaToken)) == false) {
            LOG.warn("A delta synchronization is requested, but no deltaToken is available, performing an full synchronization instead.");
            savedState = new LinkedList<>();
            return false;
        }
        Path savedStatePath = localDriveStateFile();
        if (Files.exists(savedStatePath, LinkOption.NOFOLLOW_LINKS) == false) {
            LOG.warn("A delta synchronization is requested, but no local drive state is available, performing an full synchronization instead.");
            savedState = new LinkedList<>();
            return false;
        }
        try (InputStream fis = Files.newInputStream(savedStatePath,
                                                    StandardOpenOption.READ)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            savedState = (LinkedList<LocalItem>)ois.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            throw new OneDriveException("Failure reading saved state of localdrive",
                                        e);
        }
    }

    /**
     * Serialize the local drive state to the home directory of the user
     * 
     * @throws OneDriveException of state cannot be serialized
     */
    private void writeLocalDriveState() {
        savedState.clear();
        savedState.addAll(items.values());
        try (
            OutputStream os = Files.newOutputStream(localDriveStateFile(),
                                                    StandardOpenOption.CREATE,
                                                    StandardOpenOption.WRITE)) {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(savedState);
        } catch (IOException e) {
            throw new OneDriveException("Failure writing state of localdrive",
                                        e);
        }
    }

    /**
     * Get path for (de)serialization of local drive state, uses the home
     * directory of the user in combination with the drive identifier
     * 
     * @return Path
     */
    private Path localDriveStateFile() {
        return Paths.get(System.getProperty("user.home"))
            .resolve(FILE_LOCAL_DRIVE_STATE + remoteDrive.getDriveId()
                     + ".ser");
    }

    /**
     * Determine if local item corresponds with root of the OneDrive
     * 
     * @param local LocalItem
     * @return boolean
     * @throws IOException
     */
    private boolean isLocalRoot(LocalItem local) throws IOException {
        return (ResourceType.FOLDER.equals(local.type())
                && ((LocalFolder)local).isLocalRoot());
    }

    /**
     * Register a LocalItem for addition to OneDrive.
     * 
     * @param item LocalItem
     */
    void registerAddition(LocalItem item) {
        additions.add(item);
    }

    /**
     * Register a LocalItem for presence in the LocalDrive
     * 
     * @param item LocalItem
     */
    void registerItem(LocalItem item) {
        items.put(item.getId(), item);
    }

    /**
     * Deregister a LocalItem from the LocalDrive
     * 
     * @param item LocalItem
     */
    void deregisterItem(LocalItem item) {
        items.remove(item.getId());
    }

    /**
     * Register a LocalItem for deletion on OneDrive.
     * 
     * @param item LocalFile
     */
    void registerDeletion(LocalItem item) {
        savedState.add(item);
    }

    /**
     * Register a folder or drive for lookup
     * 
     * @param folder LocalResource
     */
    void registerFolder(LocalResource folder) {
        folders.put(folder.getId(), folder);
    }

    /**
     * Get root path of local drive
     * 
     * @return Path
     */
    Path getLocalDriveRoot() {
        return localDrive.getPath();
    }
}