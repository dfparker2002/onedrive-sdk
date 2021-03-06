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

import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveContent;

/**
 * LocalDriveRepository defines a repository for storing LocalItem resources (a
 * local copy of an OneDrive item)
 * 
 * @author yucca.io
 */
public interface LocalDriveRepository {

    /**
     * Create a folder
     * 
     * @param resource LocalFolder
     * @throws IOException
     */
    void createFolder(LocalFolder resource) throws IOException;

    /**
     * Create a file
     * 
     * @param resource LocalFile
     * @throws IOException
     */
    void createFile(LocalFile resource, OneDriveContent content)
        throws IOException;

    /**
     * Delete the resource
     * 
     * @param resource LocalItem
     * @throws IOException
     */
    void delete(LocalItem resource) throws IOException;

    /**
     * Test if resource exists
     * 
     * @param resource LocalItem
     * @return true if available
     */
    boolean exists(LocalItem resource);

    /**
     * Get the LocalDrive
     * 
     * @return LocalDrive
     * @throws IOException
     */
    LocalDrive getLocalDrive();

    /**
     * Determine if folder is root of local drive
     * 
     * @param folder LocalResource
     * @return boolean true if root
     */
    boolean isLocalDriveRoot(LocalResource folder);

    /**
     * Get path to the repository
     * 
     * @return Path
     */
    Path getPath();

    /**
     * Renames the resource
     * 
     * @param resource LocalItem
     * @param name String new name
     * @throws IOException
     */
    void rename(LocalItem resource, String name) throws IOException;

    /**
     * Reset the timestamps of a resource based on field values. When a file is
     * created in a folder the underlying filesystem will update the timestamp
     * by which they are not in sync anymore with the OneDrive timestamp.
     * 
     * @param resource LocalResource
     * @throws IOException
     */
    void resetTimestamps(LocalResource resource) throws IOException;

    /**
     * Updates the metadata attributes and content of the local file. If the
     * item name was changed, the file is renamed
     * 
     * @param resource LocalItem
     * @param content OneDriveContent
     * @throws IOException
     */
    void update(LocalItem resource, OneDriveContent content) throws IOException;

    /**
     * Read the metadata for a resource
     * 
     * @param resource LocalItem
     * @throws IOException
     */
    void readMetadata(LocalItem resource) throws IOException;

    /**
     * Write the metadata for the resource
     * 
     * @param resource LocalItem
     * @throws IOException
     */
    void writeMetadata(LocalItem resource) throws IOException;

    /**
     * Get the OneDrive
     * 
     * @return OneDrive
     */
    OneDrive getOneDrive();
}
