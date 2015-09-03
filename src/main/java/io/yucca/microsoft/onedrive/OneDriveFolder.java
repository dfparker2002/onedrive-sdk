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

import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * OneDriveFolder represent a folder within a OneDrive in which items like
 * audio, files, images, videos or others folders are stored.
 * 
 * @author yucca.io
 */
public class OneDriveFolder extends OneDriveItem {

    /**
     * Construct a OneDriveFolder
     * 
     * @param api OneDriveAPIConnection connection used by the folder
     * @param itemId String identifier of the folder
     */
    public OneDriveFolder(OneDriveAPIConnection api, String itemId) {
        super(api, itemId);
    }

    OneDriveFolder(OneDriveAPIConnection api, Item item) {
        super(api, item);
    }

    /**
     * Copy this folder recursively to the destination folder
     * 
     * @param destination OneDriveFolder
     * @param name String name of the new folder, if {@code null} the same name
     *            is used
     * @return OneDriveFolder copied folder
     */
    public OneDriveFolder copy(OneDriveFolder destination, String name) {
        return new OneDriveFolder(api, api
            .copyById(itemId, name, destination.getParentRef()));
    }

    /**
     * Create a new folder inside this folder
     * 
     * @param name String name of the folder
     * @param name String name of the folder @param behaviour ConflictBehavior
     *            behaviour if a naming conflict occurs, if {@code null} then
     *            defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveFolder created folder
     */
    public OneDriveFolder createFolder(String name, ConflictBehavior behavior) {
        return new OneDriveFolder(api,
                                  api.createFolderById(name, itemId, behavior));
    }

    /**
     * Create a new folder inside this folder, if the folder already exist
     * creation fails
     * 
     * @param name String name of the folder
     * @return OneDriveFolder created folder
     */
    public OneDriveFolder createFolder(String name) {
        return createFolder(name, null);
    }

    /**
     * Get an item by path
     * 
     * @param path String path to item relative to the root folder
     * @return OneDriveItem
     */
    public OneDriveItem getItem(String path) {
        return new OneDriveItem(api, api.getMetadataByPath(path, null, null));
    }

    /**
     * Get all children in this folder
     * 
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem>
     */
    public Collection<OneDriveItem> listChildren(QueryParameters parameters) {
        List<OneDriveItem> children = new LinkedList<>();
        for (Item item : api.listChildrenInRoot(parameters)) {
            children.add(OneDriveItemFactory.build(api, item));
        }
        return children;
    }

    /**
     * Delete this folder and (recursively) all the children contents
     */
    public void delete() {
        super.delete();
    }

    /**
     * Download of a folder is unsupported
     * 
     * @throws UnsupportedOperationException
     */
    public OneDriveContent download() {
        throw new UnsupportedOperationException("download is not supported for a item type: folder");
    }

    /**
     * Move this folder to destination
     * 
     * @param destination OneDriveFolder
     * @return Item moved item
     */
    public OneDriveFolder move(OneDriveFolder destination) {
        return new OneDriveFolder(api, api
            .moveById(itemId, null, destination.getParentRef()));
    }

    /**
     * Move this folder to special folder
     * 
     * @param destination SpecialFolder
     * @return Item moved item
     */
    public OneDriveFolder move(SpecialFolder destination) {
        ItemReference ref = new ItemReference();
        ref.setPath(destination.getPath());
        return new OneDriveFolder(api, api.moveById(itemId, null, ref));
    }

    /**
     * Move this folder to root of Drive
     * 
     * @param destination OneDrive
     * @return Item moved item
     */
    public OneDriveFolder move(OneDrive destination) {
        return new OneDriveFolder(api, api
            .moveById(itemId, null, destination.getParentRef()));
    }

    /**
     * Rename the folder
     * 
     * @param name String new name
     */
    public void rename(String name) {
        super.rename(name);
    }

    /**
     * Search for items in this folder matching the query
     * 
     * @param query String search query
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem> results
     */
    public List<OneDriveItem> search(String query, QueryParameters parameters) {
        List<OneDriveItem> children = new LinkedList<>();
        for (Item item : api.searchById(itemId, query, parameters)) {
            children.add(OneDriveItemFactory.build(api, item));
        }
        return children;
    }

    /**
     * Upload the content into this folder
     * 
     * @param content OneDriveContent
     * @param name String name of the folder
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveItem uploaded item
     */
    public OneDriveItem upload(OneDriveContent content,
                               ConflictBehavior behavior) {
        return new OneDriveItem(api, api.uploadByParentId(content, itemId,
                                                          behavior));
    }

    /**
     * Upload the content into this folder, if the file already exist uploading
     * fails
     * 
     * @param content OneDriveContent
     * @return OneDriveItem uploaded item
     */
    public OneDriveItem upload(OneDriveContent content) {
        return upload(content, null);
    }

    /**
     * Get ItemReference for this Folder
     * 
     * @return ItemReference based on itemId
     */
    ItemReference getParentRef() {
        ItemReference ref = new ItemReference();
        ref.setId(itemId);
        return ref;
    }

}
