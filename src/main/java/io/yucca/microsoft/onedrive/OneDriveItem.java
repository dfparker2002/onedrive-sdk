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
package io.yucca.microsoft.onedrive;

import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * Represent an item stored on OneDrive
 *
 * @author yucca.io
 */
public interface OneDriveItem {

    /**
     * Copy this item recursively to the destination folder
     * 
     * @param destination OneDriveFolder
     * @param name String name of the new item, if {@code null} same name is
     *            used
     * @return OneDriveItem copied item
     */
    OneDriveItem copy(OneDriveFolderImpl destination, String name);

    /**
     * Delete this item
     */
    void delete();

    /**
     * Download the content
     * 
     * @return OneDriveContent
     */
    OneDriveContent download();

    /**
     * Move this item to destination
     * 
     * @param destination OneDriveFolder
     * @return OneDriveItem moved item
     */
    OneDriveItem move(OneDriveFolderImpl destination);

    /**
     * Move this item to destination
     * 
     * @param destination SpecialFolder
     * @return OneDriveItem moved item
     */
    OneDriveItem move(SpecialFolder destination);

    /**
     * Move this folder to root of Drive
     * 
     * @param destination OneDrive
     * @return OneDriveItem moved item
     */
    OneDriveItem move(OneDrive destination);

    /**
     * Rename the item
     * 
     * @param name String
     */
    void rename(String name);

    /**
     * Get item identifier
     * 
     * @return String
     */
    String getItemId();

    /**
     * Get the (cached) item resource or update them item if it was changed
     * 
     * @return Item
     */
    Item getItem();

    /**
     * Get ItemAddress
     * 
     * @return ItemAddress
     */
    ItemAddress getAddress();

}
