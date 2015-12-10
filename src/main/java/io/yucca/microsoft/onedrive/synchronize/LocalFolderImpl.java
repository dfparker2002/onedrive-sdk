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

import io.yucca.microsoft.onedrive.OneDriveFolder;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * LocalFolder acts as a local replica of a folder stored in OneDrive.
 * 
 * @author yucca.io
 */
public class LocalFolderImpl extends LocalItemImpl implements LocalFolder {

    private static final long serialVersionUID = 547616355007257412L;

    public static final String FOLDER_ROOT = "root";

    /**
     * Construct a LocalFolder. If the folder exists the OneDrive metadata
     * attributes are read,
     * 
     * @param path Path local path
     * @param repository LocalDriveRepository
     * @throws IOException if reading metadata fails
     */
    public LocalFolderImpl(Path path, LocalDriveRepository repository)
        throws IOException {
        this.name = path.getFileName().toString();
        this.path = path;
        this.repository = repository;
        if (repository.exists(this)) {
            repository.readMetadata(this);
        }
    }

    /**
     * Construct a LocalFolder. If the folder exists the OneDrive metadata
     * attributes are read,
     * 
     * @param path Path local path
     * @param item Item providing metadata
     * @param repository LocalDriveRepository
     * @throws IOException if reading metadata fails
     */
    public LocalFolderImpl(Path path, Item item,
                           LocalDriveRepository repository) throws IOException {
        this.name = path.getFileName().toString();
        this.path = path;
        this.repository = repository;
        relateWith(item);
        if (repository.exists(this)) {
            repository.readMetadata(this);
        }
    }

    /**
     * Construct a LocalFolder based on OneDriveFolder
     * 
     * @param path Path local path
     * @param folder OneDriveFolder remote folder
     * @param repository LocalDriveRepository
     */
    LocalFolderImpl(Path path, OneDriveFolder folder,
                    LocalDriveRepository repository) {
        this.path = path;
        this.repository = repository;
        relateWith(folder.getItem());
    }

    @Override
    public void create() throws IOException {
        repository.createFolder(this);
    }

    @Override
    public LocalFolder getFolder(Path path) throws IOException {
        return new LocalFolderImpl(path, repository);
    }

    @Override
    public boolean isContentModified(Item item) throws IOException {
        return ModificationStatus.NEWER.equals(lastModificationStatus(item));
    }

    @Override
    public ResourceType type() {
        return ResourceType.FOLDER;
    }

    @Override
    public boolean isLocalRoot() throws IOException {
        return repository.isLocalDriveRoot(this);
    }

    @Override
    public void update(Item item) throws IOException {
        relateWith(item);
        repository.createFolder(this);
        repository.update(this, null);
    }

    @Override
    public Path resolve(String name) {
        return path.resolve(name);
    }

    @Override
    public String toString() {
        return "LocalFolder: " + getPath() + " id: " + getId();
    }

}
