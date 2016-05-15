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
import io.yucca.microsoft.onedrive.actions.DrivesAction;
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
import io.yucca.microsoft.onedrive.resources.QuotaFacet;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

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
     * Construct an OneDriveImpl instance based on drive identifier
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API
     * @param driveId String drive identifier
     */
    public OneDriveImpl(OneDriveAPIConnection api, String driveId) {
        this.api = api;
        this.driveId = driveId;
    }

    /**
     * Construct an OneDriveImpl instance based on Drive
     * 
     * @param api connection to the OneDrive API
     * @param drive Drive
     */
    private OneDriveImpl(OneDriveAPIConnection api, Drive drive) {
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

    @Override
    public OneDriveFolder createFolder(String name,
                                       ConflictBehavior behaviour) {
        return new OneDriveFolderImpl(api, CreateAction
            .createFolderInRoot(api, name, behaviour));
    }

    @Override
    public OneDriveFolder createFolder(String name) {
        return createFolder(name, ConflictBehavior.FAIL);
    }

    @Override
    public OneDriveFolder getRootFolder() {
        MetadataAction action = new MetadataAction(api, getAddress());
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder getSpecialFolder(SpecialFolder folder,
                                           QueryParameters parameters) {
        SpecialFolderAction action = new SpecialFolderAction(api, folder,
                                                             parameters);
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder getFolder(ItemAddress address) {
        MetadataAction action = new MetadataAction(api, address);
        return new OneDriveFolderImpl(api, action.call());
    }

    @Override
    public OneDriveFolder getFolder(String path) {
        return getFolder(new PathAddress(path));
    }

    @Override
    public OneDriveItem getItem(ItemAddress address) {
        MetadataAction action = new MetadataAction(api, address);
        return new OneDriveItemImpl(api, action.call());
    }

    @Override
    public OneDriveItem getItem(String path) {
        return getItem(new PathAddress(path));
    }

    @Override
    public Collection<OneDriveItem> listChildren(QueryParameters parameters) {
        List<OneDriveItem> children = new LinkedList<>();
        ListChildrenAction action = new ListChildrenAction(api, parameters);
        for (Item item : action.call()) {
            children.add(OneDriveItemFactory.newInstance(api, item));
        }
        return children;
    }

    @Override
    public Collection<OneDriveItem> listChildren() {
        return listChildren(null);
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

    @Override
    public ItemAddress getAddress() {
        return new RootAddress();
    }

    @Override
    public Drive getDrive() {
        if (drive == null) {
            drive = new DriveAction(api, driveId).call();
        }
        return drive;
    }

    @Override
    public List<Drive> getDrives() {
        return (new DrivesAction(api)).call();
    }

    @Override
    public Identity getUser() {
        return getDrive().getOwner().getUser();
    }

    @Override
    public QuotaFacet getQuota() {
        return getDrive().getQuota();
    }

    @Override
    public String getDriveId() {
        return driveId;
    }

    @Override
    public String toString() {
        return "OneDrive: " + driveId + ", user: " + getUser().getDisplayName();
    }
}
