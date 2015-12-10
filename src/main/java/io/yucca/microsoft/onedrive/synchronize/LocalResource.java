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
import java.io.Serializable;
import java.nio.file.Path;

import io.yucca.microsoft.onedrive.resources.Item;

/**
 * LocalResource represents a local replica of an OneDrive resource (file,
 * folder or drive)
 * 
 * @author yucca.io
 */
public interface LocalResource extends Serializable {

    /**
     * Get resource id
     * 
     * @return String
     */
    String getId();

    /**
     * Set the resource id
     * 
     * @param id String
     */
    void setId(String id);

    /**
     * Get parent resource id
     * 
     * @return String
     */
    String getParentId() throws IOException;

    /**
     * Get parent resource
     * 
     * @return LocalResource
     */
    LocalResource getParent() throws IOException;

    /**
     * Get resource name
     * 
     * @return String
     */
    String getName();

    /**
     * Set the name
     * 
     * @param name String
     */
    void setName(String name);

    /**
     * Get resource path
     * 
     * @return Path
     */
    Path getPath();

    /**
     * Set the resource path
     * 
     * @param path Path
     */
    void setPath(Path path);

    /**
     * Get the type of the resource
     * 
     * @return ResourceType
     */
    ResourceType type();

    /**
     * Set the CreatedDateTime
     * 
     * @param millis long
     */
    void setCreatedDateTime(long millis);

    /**
     * Get the CreatedDateTime
     * 
     * @return long
     */
    long getCreatedDateTime();

    /**
     * Set the LastModifiedDateTime
     * 
     * @param millis long
     */
    void setLastModifiedDateTime(long millis);

    /**
     * Get the LastModifiedDateTime
     * 
     * @return long
     */
    long getLastModifiedDateTime();

    /**
     * Determine if id is set
     * 
     * @return boolean
     */
    boolean hasId();

    /**
     * Update the {@link Item} properties based on the LocalResource
     * 
     * @param item Item
     */
    void updateItem(Item item);

}
