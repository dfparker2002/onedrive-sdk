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

import io.yucca.microsoft.onedrive.facets.QuotaFacet;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Identity;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * OneDrive represent a drive in OneDrive
 *
 * @author yucca.io
 */
public class OneDrive {

    private final OneDriveAPIConnection api;

    private final String driveId;

    /**
     * Construct an OneDrive instance
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API
     * @param driveId String drive identifier
     */
    public OneDrive(OneDriveAPIConnection api, String driveId) {
        this.api = api;
        this.driveId = driveId;
    }

    OneDrive(OneDriveAPIConnection api, Drive drive) {
        this.api = api;
        this.driveId = drive.getId();
    }

    /**
     * Get the default drive for the user
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API
     * @return OneDrive
     */
    public static OneDrive defaultDrive(OneDriveAPIConnection api) {
        return new OneDrive(api, api.getDefaultDrive());
    }

    /**
     * Create a new folder inside this drive
     * 
     * @param name String name of the folder
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveFolder created folder
     */
    public OneDriveFolder createFolder(String name,
                                       ConflictBehavior behaviour) {
        return new OneDriveFolder(api, api.createFolderInRoot(name, behaviour));
    }

    /**
     * Create a new folder inside this drive, if the folder already exist
     * creation fails
     * 
     * @param name String name of the folder
     * @return created folder
     */
    public OneDriveFolder createFolder(String name) {
        return createFolder(name, null);
    }

    /**
     * Get a root folder in this drive
     * 
     * @return OneDriveFolder
     */
    public OneDriveFolder getRootFolder() {
        return new OneDriveFolder(api, api.getMetadataByPath("", null, null));
    }

    /**
     * Get a special folder in this drive
     * 
     * @param folder SpecialFolder
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return OneDriveFolder
     */
    public OneDriveFolder getSpecialFolder(SpecialFolder folder,
                                           QueryParameters parameters) {
        return new OneDriveFolder(api,
                                  api.getSpecialFolder(folder, parameters));
    }

    /**
     * Get a folder by path
     * 
     * @param path String path to folder relative to the root folder
     * @return OneDriveFolder
     */
    public OneDriveFolder getFolder(String path) {
        return new OneDriveFolder(api, api.getMetadataByPath(path, null, null));
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
     * Get all children in this drive
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
     * Get all children in this drive
     * 
     * @return Collection<OneDriveItem>
     */
    public Collection<OneDriveItem> listChildren() {
        return listChildren(null);
    }

    /**
     * Search for items in this drive matching the query
     * 
     * @param query String search query
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem> results
     */
    public List<OneDriveItem> search(String query, QueryParameters parameters) {
        List<OneDriveItem> children = new LinkedList<>();
        for (Item item : api.searchInRoot(query, parameters)) {
            children.add(OneDriveItemFactory.build(api, item));
        }
        return children;
    }

    /**
     * Get ItemReference for this Folder
     * 
     * @return ItemReference based on root path
     */
    ItemReference getParentRef() {
        ItemReference ref = new ItemReference();
        ref.setPath("/drive/root");
        // ONEDRIVE BUG using ref.setId(driveId); does not work, gives a
        // {"error":{"code":"invalidRequest","message":"ObjectHandle is
        // Invalid","innererror":{"code":"invalidResourceId"}}}
        return ref;
    }

    /**
     * Get drive resource, never cached because there is not eTag value
     * 
     * @return Drive
     */
    public Drive getDrive() {
        return api.getDrive(driveId);
    }

    /**
     * Get User information
     * 
     * @return Identity
     */
    public Identity getUser() {
        return getDrive().getOwner().getUser();
    }

    /**
     * Get Quota information
     * 
     * @return QuotaFacet
     */
    public QuotaFacet getQuota() {
        return getDrive().getQuota();
    }

    /**
     * Get drive identifier
     * 
     * @return String
     */
    public String getDriveId() {
        return driveId;
    }

    public String toString() {
        return "OneDrive: " + driveId + " user: " + getUser().getDisplayName();
    }
}
