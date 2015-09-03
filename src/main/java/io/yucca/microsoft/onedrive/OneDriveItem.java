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

/**
 * Represent an item stored on OneDrive
 * 
 * @author yucca.io
 */
public class OneDriveItem {

    protected final OneDriveAPIConnection api;

    protected final String itemId;

    protected Item item;

    /**
     * Constructor
     * 
     * @param api OneDriveAPIConnection connection used by the folder
     * @param itemId String identifier of the folder
     */
    public OneDriveItem(OneDriveAPIConnection api, String itemId) {
        this.api = api;
        this.itemId = itemId;
    }

    OneDriveItem(OneDriveAPIConnection api, Item item) {
        this.api = api;
        this.item = item;
        this.itemId = item.getId();
    }

    /**
     * Copy this item recursively to the destination folder
     * 
     * @param destination OneDriveFolder
     * @param name String name of the new item, if {@code null} same name is
     *            used
     * @return OneDriveItem copied item
     */
    public OneDriveItem copy(OneDriveFolder destination, String name) {
        return new OneDriveItem(api, api.copyById(itemId, name,
                                                  destination.getParentRef()));
    }

    /**
     * Delete this item
     */
    public void delete() {
        api.deleteById(itemId, getEtag());
    }

    /**
     * Download the content
     * <p>
     * FIXME [ec]tag handling?
     * </p>
     * 
     * @return OneDriveContent
     */
    public OneDriveContent download() {
        return api.downloadById(itemId, null);
    }

    /**
     * Move this item to destination
     * 
     * @param destination OneDriveFolder
     * @return OneDriveItem moved item
     */
    public OneDriveItem move(OneDriveFolder destination) {
        return new OneDriveItem(api, api.moveById(itemId, null,
                                                  destination.getParentRef()));
    }

    /**
     * Rename the item
     * 
     * @param name String
     */
    public void rename(String name) {
        Item changed = (item == null) ? changed = new Item(itemId) : item;
        changed.setName(name);
        this.item = api.update(changed, getEtag());
    }

    public String getItemId() {
        return itemId;
    }

    /**
     * Get the (cached) item resource or update if it was changed
     * 
     * @return Item
     */
    public Item getItem() {
        try {
            this.item = api.getMetadataById(itemId, getEtag(), null);
        } catch (NotModifiedException e) {
            // do nothing return cached item
        }
        return item;
    }

    /**
     * Get the eTag of the Item
     * 
     * @return String
     */
    String getEtag() {
        return (item == null) ? null : item.geteTag();
    }

    /**
     * Get the cTag of the Item
     * 
     * @return String
     */
    String getCtag() {
        return (item == null) ? null : item.getcTag();
    }

}
