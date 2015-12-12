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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.yucca.microsoft.onedrive.actions.CopyAction;
import io.yucca.microsoft.onedrive.actions.CreateAction;
import io.yucca.microsoft.onedrive.actions.ListChildrenAction;
import io.yucca.microsoft.onedrive.actions.MetadataAction;
import io.yucca.microsoft.onedrive.actions.MoveAction;
import io.yucca.microsoft.onedrive.actions.PollAction;
import io.yucca.microsoft.onedrive.actions.SearchAction;
import io.yucca.microsoft.onedrive.actions.UploadAction;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.addressing.SpecialAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * OneDriveFolder represent a folder within a OneDrive in which items like
 * audio, files, images, videos or others folders are stored.
 * 
 * @author yucca.io
 */
public class OneDriveFolderImpl extends OneDriveItemImpl
    implements OneDriveFolder {

    /**
     * Construct an OneDriveFolderImpl instance based on folder identifier
     * 
     * @param api OneDriveAPIConnection connection used by the folder
     * @param itemId String identifier of the folder
     */
    public OneDriveFolderImpl(OneDriveAPIConnection api, String itemId) {
        super(api, itemId);
    }

    /**
     * Construct an OneDriveFolderImpl instance based on item
     * 
     * @param api OneDriveAPIConnection connection used by the folder
     * @param item Item
     */
    OneDriveFolderImpl(OneDriveAPIConnection api, Item item) {
        super(api, item);
    }

    @Override
    public OneDriveFolder copy(OneDriveFolder destination, String name) {
        CopyAction action = new CopyAction(api, getAddress(), name,
                                           destination.getAddress());
        PollAction pollAction = new PollAction(api, action.call(), getAddress(),
                                               CopyAction.ACTION);
        return new OneDriveFolderImpl(api, pollAction.call());
    }

    @Override
    public OneDriveFolder createFolder(String name, ConflictBehavior behavior) {
        CreateAction action = new CreateAction(api, name, getAddress(),
                                               behavior);
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder createFolder(String name) {
        return createFolder(name, null);
    }

    @Override
    public OneDriveFolder getFolder(ItemAddress address) {
        MetadataAction action = new MetadataAction(api, address, getEtag());
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder getFolder(String path) {
        return getFolder(new PathAddress(getItem(), path));
    }

    @Override
    public OneDriveItem getItem(ItemAddress address) {
        MetadataAction action = new MetadataAction(api, address, getEtag());
        return new OneDriveItemImpl(api, action.call());
    }

    @Override
    public OneDriveItem getItem(String path) {
        return getItem(new PathAddress(getItem(), path));
    }

    @Override
    public Collection<OneDriveItem> listChildren(QueryParameters parameters) {
        List<OneDriveItem> children = new LinkedList<>();
        ListChildrenAction action = new ListChildrenAction(api, getAddress(),
                                                           null, parameters);
        for (Item item : action.call()) {
            children.add(OneDriveItemFactory.newInstance(api, item));
        }
        return children;
    }

    @Override
    public void delete() {
        super.delete();
    }

    @Override
    public OneDriveContent download() {
        throw new UnsupportedOperationException("download is not supported for a item type: folder");
    }

    @Override
    public OneDriveFolder move(OneDriveFolder destination) {
        MoveAction action = new MoveAction(api, getAddress(), null,
                                           destination.getAddress());
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder move(SpecialFolder destination) {
        MoveAction action = new MoveAction(api, getAddress(), null,
                                           new SpecialAddress(destination));
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder move(OneDrive destination) {
        MoveAction action = new MoveAction(api, getAddress(), null,
                                           new RootAddress());
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public void rename(String name) {
        super.rename(name);
    }

    @Override
    public List<OneDriveItem> search(String query, QueryParameters parameters) {
        List<OneDriveItem> children = new LinkedList<>();
        SearchAction action = new SearchAction(api, getAddress(), query,
                                               parameters);
        for (Item item : action.call()) {
            children.add(OneDriveItemFactory.newInstance(api, item));
        }
        return children;
    }

    @Override
    public OneDriveItem upload(OneDriveContent content,
                               ConflictBehavior behavior) {
        UploadAction action = new UploadAction(api, content, getAddress(),
                                               behavior);
        return new OneDriveItemImpl(api, action.call());
    }

    @Override
    public OneDriveItem upload(OneDriveContent content) {
        return upload(content, ConflictBehavior.FAIL);
    }

}
