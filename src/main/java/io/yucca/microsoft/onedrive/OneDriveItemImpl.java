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

import io.yucca.microsoft.onedrive.actions.CopyAction;
import io.yucca.microsoft.onedrive.actions.DeleteAction;
import io.yucca.microsoft.onedrive.actions.DownloadAction;
import io.yucca.microsoft.onedrive.actions.MetadataAction;
import io.yucca.microsoft.onedrive.actions.MoveAction;
import io.yucca.microsoft.onedrive.actions.PollAction;
import io.yucca.microsoft.onedrive.actions.UpdateAction;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.addressing.SpecialAddress;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * Represent an item stored in OneDrive
 * 
 * @author yucca.io
 */
public class OneDriveItemImpl implements OneDriveItem {

    protected final OneDriveAPIConnection api;

    protected final String itemId;

    protected Item item;

    /**
     * Construct an OneDriveItemImpl instance based on item identifier
     * 
     * @param api OneDriveAPIConnection connection used by the item
     * @param itemId String identifier of the item
     */
    public OneDriveItemImpl(OneDriveAPIConnection api, String itemId) {
        this.api = api;
        this.itemId = itemId;
    }

    /**
     * Construct an OneDriveItemImpl based on an item
     * 
     * @param api OneDriveAPIConnection connection used by the item
     * @param item Item
     */
    OneDriveItemImpl(OneDriveAPIConnection api, Item item) {
        this.api = api;
        this.item = item;
        this.itemId = item.getId();
    }

    @Override
    public OneDriveItem copy(OneDriveFolder destination, String name) {
        CopyAction action = new CopyAction(api, getAddress(), name,
                                           destination.getAddress());
        PollAction pollAction = new PollAction(api, action.call(), getAddress(),
                                               CopyAction.ACTION);
        return new OneDriveItemImpl(api, pollAction.call());
    }

    @Override
    public void delete() {
        new DeleteAction(api, getAddress(), getEtag()).call();
    }

    /**
     * Download the content
     * <p>
     * FIXME [ec]tag handling?
     * </p>
     * 
     * @return OneDriveContent
     */
    @Override
    public OneDriveContent download() {
        return new DownloadAction(api, getAddress()).call();
    }

    @Override
    public OneDriveItem move(OneDriveFolder destination) {
        MoveAction action = new MoveAction(api, getAddress(), null,
                                           destination.getAddress());
        return new OneDriveItemImpl(api, action.call());
    }

    @Override
    public OneDriveItem move(OneDrive destination) {
        MoveAction action = new MoveAction(api, getAddress(), null,
                                           new RootAddress());
        return new OneDriveItemImpl(api, action.call());
    }

    @Override
    public OneDriveItem move(SpecialFolder destination) {
        MoveAction action = new MoveAction(api, getAddress(), null,
                                           new SpecialAddress(destination));
        return new OneDriveItemImpl(api, action.call());
    }

    @Override
    public void rename(String name) {
        Item changed = item;
        if (item == null) {
            changed = new Item(itemId);
        }
        changed.setName(name);
        this.item = new UpdateAction(api, changed).call();
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public Item getItem() {
        try {
            MetadataAction action = new MetadataAction(api, getAddress(),
                                                       getEtag(), null);
            this.item = action.call();
        } catch (NotModifiedException e) {
            // do nothing return cached item
        }
        return item;
    }

    @Override
    public ItemAddress getAddress() {
        return new IdAddress(itemId);
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
