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
import java.nio.file.Path;
import java.text.ParseException;

import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Represents a folder on the local filesystem
 * 
 * @author yucca.io
 */
public class LocalFolder extends LocalFile {

    private static final long serialVersionUID = 547616355007257412L;

    public static final String FOLDER_ROOT = "root";

    /**
     * Construct a LocalFolder based on path, if the folder exists, the OneDrive
     * metadata attributes are read from the filesystem
     * 
     * @param folder Path
     * @throws IOException if reading metadata fails
     */
    public LocalFolder(Path folder) throws IOException {
        super(folder);
    }

    /**
     * Construct a LocalFolder based on Item to be persisted in LocalResource.
     * The OneDrive metadata attributes are provided by the Item
     * 
     * @param item Item
     * @param parent LocalResource
     * @throws IOException if reading/writing metadata if creation fails
     * @throws ParseException if parsing of last modification timestamp fails
     */
    public LocalFolder(Item item, LocalResource parent)
        throws IOException, ParseException {
        super(item, parent);
        create(parent);
    }

    /**
     * Create the folder and write metadata attributes
     * 
     * @throws IOException
     */
    private void create(LocalResource parent) throws IOException {
        if (!exists()) {
            Files.createDirectory(path);
        }
        writeMetadata();
        parent.resetTimestamps();
    }

    /**
     * Only updates folder metadata attributes
     * 
     * @param content OneDriveContent content is ignored and should be
     *            {@code null}
     * @throws IOException if writing metadata fails
     */
    @Override
    public void update(Item item, OneDriveContent content, LocalResource parent)
        throws IOException {
        try {
            relateWith(item);
        } catch (ParseException e) {
            throw new IOException("Failure parsing item timestamps", e);
        }
        writeMetadata();
        rename(item.getName());
        parent.resetTimestamps();
    }

    /**
     * Determines if the last modification date of this folder is newer than
     * item,
     * 
     * @return true if newer
     */
    @Override
    public boolean isContentModified(Item item) throws IOException {
        return ModificationStatus.NEWER.equals(lastModificationStatus(item));
    }

    @Override
    public ResourceType type() {
        return ResourceType.FOLDER;
    }

    /**
     * Determine if this folder is the local root folder
     * 
     * @return boolean
     * @throws IOException
     */
    public boolean isLocalRoot() throws IOException {
        // TODO root marker
        return FOLDER_ROOT.equals(name);
        // && LocalDrive.ONEDRIVE_ROOT_MARKER.equals(getParentId()));
    }

    public String toString() {
        return "LocalFolder: " + getPath() + " id: " + getId();
    }

}
