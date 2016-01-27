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

import java.io.FileNotFoundException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;

/**
 * FileSystemSynchronizer
 * 
 * @author yucca.io
 */
public class FileSystemSynchronizer implements LocalDriveSynchronizer {

    private static final Logger LOG = LoggerFactory
        .getLogger(FileSystemSynchronizer.class);

    public static final String LOCAL_DRIVE_STATE = ".onedrivestate_";

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
    private final Map<String, LocalFolder> folders = new HashMap<>();

    /**
     * Map holding the previous state of the LocalDrive, used to determine
     * localy deleted items
     */
    private List<LocalItem> savedState;

    private String itemId;

    private final LocalDriveRepository repository;

    public FileSystemSynchronizer(Path localPath, OneDrive onedrive)
        throws IOException {
        this.repository = new FileSystemRepository(localPath, onedrive);
    }

    /**
     * FileSystemSynchronizer
     * 
     * @param repository LocalDriveRepository
     */
    public FileSystemSynchronizer(LocalDriveRepository repository) {
        this.repository = repository;
        // TODO https://github.com/robses/onedrive-sdk/issues/10
        // prevent accidental deletion of complete OneDrive if LocalDrive is not
        // present. if root folder does not exist but state is available stop,
        // probably localdrive is deleted
    }

    @Override
    public void initializeSession(boolean useSavedState, LocalFolder folder) {
        this.itemId = folder.getId();
        initializeState(useSavedState);
        registerFolder(folder);
        walkPath(folder);
    }

    @Override
    public void clearSession() {
        additions.clear();
        items.clear();
        folders.clear();
    }

    @Override
    public void saveSession() {
        Path savedStatePath = localDriveStateFile();
        LOG.info("Writing state to file: {}", savedStatePath);
        try (
            OutputStream os = Files.newOutputStream(savedStatePath,
                                                    StandardOpenOption.CREATE,
                                                    StandardOpenOption.WRITE)) {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(new LinkedList<>(items.values()));
        } catch (IOException e) {
            throw new OneDriveException("Failure writing local state to file: "
                                        + savedStatePath, e);
        }
    }

    /**
     * Walk the LocalDrive enumerating files and folders
     * 
     * @param folder LocalFolder to walk
     * @throws OneDriveException if walking fails
     */
    private void walkPath(LocalFolder folder) {
        try {
            LOG.info("Walking the LocalDrive: {}, enumerate files and folders used in synchronization",
                     folder.getPath());
            LocalFileVisitor visitor = new LocalFileVisitor(this);
            Files.walkFileTree(folder.getPath(), new HashSet<FileVisitOption>(),
                               Integer.MAX_VALUE, visitor);
        } catch (IOException e) {
            throw new OneDriveException("Failure enumerating LocalDrive: "
                                        + folder.getPath(), e);
        }
    }

    /**
     * Reads the state
     * 
     * @param useSavedState boolean true to read the saved state otherwise
     *            create a clean state
     */
    private void initializeState(boolean useSavedState) {
        if (useSavedState) {
            try {
                savedState = deserializeState();
            } catch (FileNotFoundException e) {
                LOG.warn("State file could not be read, no previous state is available, therefor a full synchronization is performed.",
                         e);
                savedState = new LinkedList<>();
            }
        } else {
            savedState = new LinkedList<>();
        }
    }

    /**
     * Deserialize the local drive state on delta synchronization, or
     * instantiate a new state Map if no deltaToken or no local drive state is
     * available
     *
     * @throws FileNotFoundException if state file does not exists
     * @throws OneDriveException of state cannot be deserialized
     */
    @SuppressWarnings("unchecked")
    private List<LocalItem> deserializeState() throws FileNotFoundException {
        Path savedStatePath = localDriveStateFile();
        if (!Files.exists(savedStatePath, LinkOption.NOFOLLOW_LINKS)) {
            throw new FileNotFoundException("Local state file " + savedStatePath
                                            + " does not exist.");
        }
        LOG.info("Reading Local state from file: {}", savedStatePath);
        try (InputStream fis = Files.newInputStream(savedStatePath,
                                                    StandardOpenOption.READ)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (LinkedList<LocalItem>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new OneDriveException("Failure reading local state file: "
                                        + savedStatePath, e);
        }
    }

    /**
     * Get path for (de)serialization of local drive state, using the home
     * directory of the user in combination with the drive or item identifier.
     * 
     * @return Path
     */
    private Path localDriveStateFile() {
        return Paths.get(System.getProperty("user.home"))
            .resolve(LOCAL_DRIVE_STATE + itemId + ".ser");
    }

    @Override
    public void createFolder(LocalFolder resource) throws IOException {
        repository.createFolder(resource);
        registerItem(resource);
    }

    @Override
    public void createFile(LocalFile resource, OneDriveContent content)
        throws IOException {
        repository.createFile(resource, content);
        registerItem(resource);
    }

    @Override
    public void delete(LocalItem resource) throws IOException {
        repository.delete(resource);
        deregisterItem(resource);
    }

    @Override
    public boolean exists(LocalItem resource) {
        return repository.exists(resource);
    }

    @Override
    public void rename(LocalItem resource, String name) throws IOException {
        repository.rename(resource, name);
    }

    @Override
    public void update(LocalItem resource, OneDriveContent content)
        throws IOException {
        repository.update(resource, content);
        if (ResourceType.FOLDER.equals(resource.type())) {
            registerFolder((LocalFolder)resource);
        }
        // register a localy created item, so it is added to
        // the saved state of the LocalDrive
        registerItem(resource);
    }

    @Override
    public List<LocalItem> getAdditions() {
        return additions;
    }

    @Override
    public List<LocalItem> getDeletions() {
        List<LocalItem> deletions = new ArrayList<>();
        for (LocalItem local : savedState) {
            if (!items.containsKey(local.getId())) {
                deletions.add(local);
            }
        }
        return deletions;
    }

    @Override
    public LocalDrive getLocalDrive() {
        return repository.getLocalDrive();
    }

    @Override
    public LocalFolder getLocalFolder(String id) {
        LocalFolder folder = folders.get(id);
        if (folder == null) {
            throw new OneDriveException("Folder with id: " + id
                                        + " does not exist in LocalDrive.");
        }
        return folder;
    }

    @Override
    public Path getPath() {
        return repository.getPath();
    }

    @Override
    public LocalItem getLocalItem(String id) {
        return items.get(id);
    }

    /**
     * Register a LocalItem for addition to OneDrive.
     * 
     * @param item LocalItem
     */
    @Override
    public void registerAddition(LocalItem item) {
        additions.add(item);
    }

    /**
     * Register a LocalItem for presence in the LocalDrive
     * 
     * @param item LocalItem
     */
    @Override
    public void registerItem(LocalItem item) {
        items.put(item.getId(), item);
    }

    /**
     * Deregister a LocalItem from the LocalDrive
     * 
     * @param item LocalItem
     */
    @Override
    public void deregisterItem(LocalItem item) {
        items.remove(item.getId());
    }

    /**
     * Register a LocalItem for deletion on OneDrive.
     * 
     * @param item LocalFile
     */
    @Override
    public void registerDeletion(LocalItem item) {
        savedState.add(item);
    }

    /**
     * Register a folder or drive for lookup
     * 
     * @param folder LocalFolder
     */
    @Override
    public void registerFolder(LocalFolder folder) {
        folders.put(folder.getId(), folder);
    }

    @Override
    public boolean isLocalDriveRoot(LocalResource folder) {
        return repository.isLocalDriveRoot(folder);
    }

    @Override
    public void resetTimestamps(LocalResource resource) throws IOException {
        repository.resetTimestamps(resource);
    }

    @Override
    public void readMetadata(LocalItem resource) throws IOException {
        repository.readMetadata(resource);
    }

    @Override
    public void writeMetadata(LocalItem resource) throws IOException {
        repository.writeMetadata(resource);
    }

    @Override
    public OneDrive getOneDrive() {
        return repository.getOneDrive();
    }

}
