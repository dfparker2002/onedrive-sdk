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
 * Represent a LocalResource like a file, folder or drive
 * 
 * @author yucca.io
 */
public interface LocalResource extends Serializable {

    String ATTRIBUTE_ONEDRIVE_ITEMID = "onedrive.id";

    /**
     * Get resource id
     * 
     * @return String
     */
    String getId();

    /**
     * Get parent resource id
     * 
     * @return String
     */
    String getParentId() throws IOException;

    /**
     * Get resource name
     * 
     * @return String
     */
    String getName();

    /**
     * Get resource path
     * 
     * @return Path
     */
    Path getPath();

    /**
     * Get the type of the resource
     * 
     * @return ResourceType
     */
    ResourceType type();

    /**
     * Determine if id is set
     * 
     * @return boolean
     */
    boolean hasId();

    /**
     * Update the {@link Item} properties based on this LocalResource
     * 
     * @param item Item
     */
    void updateItem(Item item);

    /**
     * Reset the timestamps of a resource based on field values. When a file is
     * created in a folder the underlying filesystem will update the timestamp
     * by which they are not in sync anymore with the OneDrive timestamp.
     */
    void resetTimestamps() throws IOException;
}
