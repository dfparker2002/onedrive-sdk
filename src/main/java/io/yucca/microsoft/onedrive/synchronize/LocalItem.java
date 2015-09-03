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

import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * Represent a LocalItem like a file or folder
 * 
 * @author yucca.io
 */
public interface LocalItem extends LocalResource {

    /**
     * Determine if item exists
     * 
     * @return boolean
     */
    boolean exists();

    /**
     * Update the content and metadata of this local item
     * 
     * @param content OneDriveContent updated content
     * @throws IOException if writing fails
     */
    void update(Item item, OneDriveContent content, LocalResource parent)
        throws IOException;

    /**
     * Deletes this local item
     * 
     * @throws IOException if deletion fails
     */
    void delete() throws IOException;

    /**
     * Rename this local item
     * 
     * @param name String new name
     * @throws IOException if rename fails
     */
    void rename(String name) throws IOException;

    /**
     * Determine modification status in regard to {@link Item}
     * 
     * @param item Item
     * @return ModificationStatus if item has invalid date
     *         {@link ModificationStatus#NOTMODIFIED} should be returned
     */
    ModificationStatus lastModificationStatus(Item item);

    /**
     * Determine if this local item content has been modified with regared to
     * {@link Item}
     * 
     * @param item Item
     * @return true if content differs
     * @throws IOException if file cannot be read
     */
    boolean isContentModified(Item item) throws IOException;

}
