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

import io.yucca.microsoft.onedrive.actions.CreateAction;
import io.yucca.microsoft.onedrive.actions.DriveAction;
import io.yucca.microsoft.onedrive.actions.ListChildrenAction;
import io.yucca.microsoft.onedrive.actions.MetadataAction;
import io.yucca.microsoft.onedrive.actions.SearchAction;
import io.yucca.microsoft.onedrive.actions.SpecialFolderAction;
import io.yucca.microsoft.onedrive.actions.UploadAction;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.PathAddress;
import io.yucca.microsoft.onedrive.addressing.RootAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Identity;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;
import io.yucca.microsoft.onedrive.resources.facets.QuotaFacet;

/**
 * OneDrive represents a drive in OneDrive
 *
 * @author yucca.io
 */
public class OneDriveImpl implements OneDrive {

    private final OneDriveAPIConnection api;

    private final String driveId;

    private Drive drive;

    /**
     * Construct an OneDrive instance
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API
     * @param driveId String drive identifier
     */
    public OneDriveImpl(OneDriveAPIConnection api, String driveId) {
        this.api = api;
        this.driveId = driveId;
    }

    OneDriveImpl(OneDriveAPIConnection api, Drive drive) {
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
        DriveAction action = new DriveAction(api);
        return new OneDriveImpl(api, action.call());
    }

    /**
     * Get the default drive for the user
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API
     * @param driveId String drive identifier
     * @return OneDrive
     */
    public static OneDrive byDriveId(OneDriveAPIConnection api,
                                     String driveId) {
        DriveAction action = new DriveAction(api, driveId);
        return new OneDriveImpl(api, action.call());
    }

    /**
     * Create a new folder inside this drive
     * 
     * @param name String name of the folder
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaulting to
     *            {@link ConflictBehavior#FAIL}
     * @return OneDriveFolder created folder
     */
    @Override
    public OneDriveFolderImpl createFolder(String name,
                                           ConflictBehavior behaviour) {
        return new OneDriveFolderImpl(api, CreateAction
            .createFolderInRoot(api, name, behaviour));
    }

    /**
     * Create a new folder inside this drive, if the folder already exist
     * creation fails
     * 
     * @param name String name of the folder
     * @return created folder
     */
    @Override
    public OneDriveFolderImpl createFolder(String name) {
        return createFolder(name, ConflictBehavior.FAIL);
    }

    /**
     * Get the root folder of this drive
     * 
     * @return OneDriveFolder
     */
    @Override
    public OneDriveFolderImpl getRootFolder() {
        MetadataAction action = new MetadataAction(api, getAddress());
        return new OneDriveFolderImpl(api, action.call());
    }

    /**
     * Get a special folder in this drive
     * 
     * @param folder SpecialFolder
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return OneDriveFolder
     */
    @Override
    public OneDriveFolderImpl getSpecialFolder(SpecialFolder folder,
                                               QueryParameters parameters) {
        SpecialFolderAction action = new SpecialFolderAction(api, folder,
                                                             parameters);
        return new OneDriveFolderImpl(api, action.call());
    }

    /**
     * Get a folder by address
     * 
     * @param address ItemAddress address of Item
     * @return OneDriveFolder
     */
    private OneDriveFolderImpl getFolder(ItemAddress address) {
        MetadataAction action = new MetadataAction(api, address);
        return new OneDriveFolderImpl(api, action.call());
    }

    /**
     * Get a folder by path
     * 
     * @param path String path to a folder relative to the drive root i.e.
     *            "Documents"
     * @return OneDriveFolder
     */
    @Override
    public OneDriveFolderImpl getFolder(String path) {
        return getFolder(new PathAddress(path));
    }

    /**
     * Get an item by address
     * 
     * @param address ItemAddress address of Item
     * @return OneDriveItem
     */
    private OneDriveItem getItem(ItemAddress address) {
        MetadataAction action = new MetadataAction(api, address);
        return new OneDriveItemImpl(api, action.call());
    }

    /**
     * Get an item by path
     * 
     * @param path String path to item relative to the drive root
     * @return OneDriveItem
     */
    @Override
    public OneDriveItem getItem(String path) {
        return getItem(new PathAddress(path));
    }

    /**
     * Get all children in this drive
     * 
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem>
     */
    @Override
    public Collection<OneDriveItemImpl> listChildren(QueryParameters parameters) {
        List<OneDriveItemImpl> children = new LinkedList<>();
        ListChildrenAction action = new ListChildrenAction(api, parameters);
        for (Item item : action.call()) {
            children.add(OneDriveItemFactory.build(api, item));
        }
        return children;
    }

    /**
     * Get all children in this drive
     * 
     * @return Collection<OneDriveItem>
     */
    @Override
    public Collection<OneDriveItemImpl> listChildren() {
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
    @Override
    public List<OneDriveItemImpl> search(String query,
                                         QueryParameters parameters) {
        List<OneDriveItemImpl> children = new LinkedList<>();
        SearchAction action = new SearchAction(api, getAddress(), query,
                                               parameters);
        for (Item item : action.call()) {
            children.add(OneDriveItemFactory.build(api, item));
        }
        return children;
    }

    /**
     * Upload the content into this folder
     * 
     * @param content OneDriveContent
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveItem uploaded item
     */
    @Override
    public OneDriveItem upload(OneDriveContent content,
                               ConflictBehavior behavior) {
        UploadAction action = new UploadAction(api, content, getAddress(),
                                               behavior);
        return new OneDriveItemImpl(api, action.call());
    }

    /**
     * Upload the content into this folder, if the file already exist uploading
     * fails
     * 
     * @param content OneDriveContent
     * @return OneDriveItem uploaded item
     */
    @Override
    public OneDriveItem upload(OneDriveContent content) {
        return upload(content, ConflictBehavior.FAIL);
    }

    /**
     * Get ItemAddress
     * 
     * @return ItemAddress
     */
    @Override
    public ItemAddress getAddress() {
        return new RootAddress();
    }

    /**
     * Get drive resource and cache it. This will become stale over time,
     * because no eTag value if available to detect modifications of the entity
     * 
     * @return Drive
     */
    @Override
    public Drive getDrive() {
        if (drive == null) {
            drive = new DriveAction(api, driveId).call();
        }
        return drive;
    }

    /**
     * Get User information
     * 
     * @return Identity
     */
    @Override
    public Identity getUser() {
        return getDrive().getOwner().getUser();
    }

    /**
     * Get Quota information
     * 
     * @return QuotaFacet
     */
    @Override
    public QuotaFacet getQuota() {
        return getDrive().getQuota();
    }

    /**
     * Get drive identifier
     * 
     * @return String
     */
    @Override
    public String getDriveId() {
        return driveId;
    }

    @Override
    public String toString() {
        return "OneDrive: " + driveId + ", user: " + getUser().getDisplayName();
    }
}
