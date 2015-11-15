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

import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * LocalDrive acts as a local replica of an OneDrive.
 * 
 * @author yucca.io
 */
public class LocalDriveImpl extends LocalFolderImpl implements LocalDrive {

    private static final long serialVersionUID = 4994605863666178424L;

    public static final String ONEDRIVE = "onedrive";

    public static final String ONEDRIVE_ROOT_MARKER = "localroot";

    private static final String ONEDRIVE_ID_POSTFIX = "!0";

    private Drive drive;

    /**
     * Construct a LocalDrive corresponding with the root of OneDrive
     * 
     * @param drive Drive
     * @param repository LocalDriveRepository
     */
    public LocalDriveImpl(Drive drive, LocalDriveRepository repository)
        throws IOException {
        super(repository.getPath(), repository);
        this.drive = drive;
    }

    @Override
    public String getId() {
        return drive.getId().toUpperCase() + ONEDRIVE_ID_POSTFIX;
    }

    @Override
    public String getParentId() throws IOException {
        return ONEDRIVE_ROOT_MARKER;
    }

    @Override
    public String getName() {
        return ONEDRIVE;
    }

    @Override
    public Path getPath() {
        return repository.getPath();
    }

    @Override
    public ResourceType type() {
        return ResourceType.DRIVE;
    }

    @Override
    public boolean hasId() {
        return getId() != null;
    }

    @Override
    public void updateItem(Item item) {
        return;
    }

    @Override
    public String toString() {
        return "LocalDrive: " + getId() + " path: " + getPath();
    }

}
