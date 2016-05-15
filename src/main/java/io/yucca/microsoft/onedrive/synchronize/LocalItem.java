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

import io.yucca.microsoft.onedrive.resources.Item;

/**
 * LocalItem acts a local replica of an item stored in OneDrive (file or folder)
 * 
 * @author yucca.io
 */
public interface LocalItem extends LocalResource {

    /**
     * Create the item
     * 
     * @throws IOException
     */
    void create() throws IOException;

    /**
     * Deletes the item
     * 
     * @throws IOException if deletion fails
     */
    void delete() throws IOException;

    /**
     * Determine if item exists
     * 
     * @return boolean
     */
    boolean exists();

    /**
     * Determine if the item has been modified with regards to the Item, this is
     * done by comparing the sha1 hashes of the file and the item
     * 
     * @param item Item
     * @return booelan true is modified
     */
    boolean isContentModified(Item item) throws IOException;

    /**
     * Determine modification status in regard to {@link Item}
     * 
     * @param item Item
     * @return ModificationStatus if item has invalid date
     *         {@link ModificationStatus#NOTMODIFIED} should be returned
     */
    ModificationStatus lastModificationStatus(Item item);

    /**
     * Rename this local item
     * 
     * @param name String new name
     * @throws IOException if rename fails
     */
    void rename(String name) throws IOException;

    /**
     * Only updates folder metadata attributes based on Item
     * 
     * @param item Item
     * @throws IOException if writing metadata fails
     */
    void update(Item item) throws IOException;

    /**
     * Relate this folder with the OneDrive item, setting the metadata
     * properties.
     * 
     * @param item Item
     */
    void relateWith(Item item);

}
