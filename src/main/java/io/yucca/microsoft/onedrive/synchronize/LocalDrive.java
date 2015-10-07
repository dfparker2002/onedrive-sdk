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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * LocalDrive represent the local root of an OneDrive
 * 
 * @author yucca.io
 */
public class LocalDrive implements LocalResource {

    private static final long serialVersionUID = 4994605863666178424L;

    public static final String ONEDRIVE = "onedrive";

    public static final String ONEDRIVE_ROOT_MARKER = "localroot";

    private static final String ONEDRIVE_ID_POSTFIX = "!0";

    private transient final Path path;

    private String id;

    /**
     * Construct a LocalDrive corresponding with the root of remote OneDrive
     * 
     * @param path Path base path to the
     * @param drive Drive
     * @throws IOException
     */
    public LocalDrive(Path path, Drive drive) throws IOException {
        this.path = path;
        this.id = drive.getId().toUpperCase() + ONEDRIVE_ID_POSTFIX;
        if (exists() == false) {
            create();
        }
        hasExtendedAttributesCapability();
        writeMetadata();
    }

    private void create() throws IOException {
        if (exists() == false) {
            Files.createDirectory(path);
        }
        writeMetadata();
    }

    public boolean exists() {
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    private void hasExtendedAttributesCapability() {
        try {
            MetadataUtil.readAttribute(path, ATTRIBUTE_ONEDRIVE_ITEMID);
        } catch (UnsupportedOperationException | IOException e) {
            throw new OneDriveException("The extended attributes capability is disabled for the filesystem or cannot be determined."
                                        + "This which is mandatory for the synchronization process to function, this depends on "
                                        + "extended attributes to relate the local item with the corresponding OneDrive item by  "
                                        + "storing the Item id as extended attribute. Enable this on Linux by remounting the partition "
                                        + "with user_xattr flag. For more information see https://docs.oracle.com/javase/tutorial/essential/io/fileAttr.html#user",
                                        e);
        }
    }

    @Override
    public String getId() {
        return id;
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
        return path;
    }

    @Override
    public ResourceType type() {
        return ResourceType.DRIVE;
    }

    @Override
    public boolean hasId() {
        return (this.id != null);
    }

    @Override
    public void updateItem(Item item) {
        return;
    }

    @Override
    public void resetTimestamps() throws IOException {
        return;
    }

    @SuppressWarnings("unused")
    private void readMetadata() throws IOException {
        this.id = MetadataUtil.readAttribute(path, ATTRIBUTE_ONEDRIVE_ITEMID);
    }

    private void writeMetadata() throws IOException {
        MetadataUtil.writeAttribute(path, ATTRIBUTE_ONEDRIVE_ITEMID, id);
    }

    public String toString() {
        return "LocalDrive: " + getId() + " path: " + getPath();
    }

}
