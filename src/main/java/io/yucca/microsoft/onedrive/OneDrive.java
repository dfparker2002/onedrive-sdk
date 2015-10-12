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
import java.util.List;

import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Drive;
import io.yucca.microsoft.onedrive.resources.Identity;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;
import io.yucca.microsoft.onedrive.resources.facets.QuotaFacet;

/**
 * OneDrive represents a drive in OneDrive
 *
 * @author yucca.io
 */
public interface OneDrive {

    /**
     * Create a new folder inside this drive
     * 
     * @param name String name of the folder
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaulting to
     *            {@link ConflictBehavior#FAIL}
     * @return OneDriveFolder created folder
     */
    OneDriveFolder createFolder(String name, ConflictBehavior behaviour);

    /**
     * Create a new folder inside this drive, if the folder already exist
     * creation fails
     * 
     * @param name String name of the folder
     * @return created folder
     */
    OneDriveFolderImpl createFolder(String name);

    /**
     * Get the root folder of this drive
     * 
     * @return OneDriveFolder
     */
    OneDriveFolderImpl getRootFolder();

    /**
     * Get a special folder in this drive
     * 
     * @param folder SpecialFolder
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return OneDriveFolder
     */
    OneDriveFolderImpl getSpecialFolder(SpecialFolder folder,
                                    QueryParameters parameters);

    /**
     * Get a folder by path
     * 
     * @param path String path to a folder relative to the drive root i.e.
     *            "Documents"
     * @return OneDriveFolder
     */
    OneDriveFolderImpl getFolder(String path);

    /**
     * Get an item by path
     * 
     * @param path String path to item relative to the drive root
     * @return OneDriveItem
     */
    OneDriveItem getItem(String path);

    /**
     * Get all children in this drive
     * 
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem>
     */
    Collection<OneDriveItemImpl> listChildren(QueryParameters parameters);

    /**
     * Get all children in this drive
     * 
     * @return Collection<OneDriveItem>
     */
    Collection<OneDriveItemImpl> listChildren();

    /**
     * Search for items in this drive matching the query
     * 
     * @param query String search query
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem> results
     */
    List<OneDriveItemImpl> search(String query, QueryParameters parameters);

    /**
     * Upload the content into this folder
     * 
     * @param content OneDriveContent
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveItem uploaded item
     */
    OneDriveItem upload(OneDriveContent content, ConflictBehavior behavior);

    /**
     * Upload the content into this folder, if the file already exist uploading
     * fails
     * 
     * @param content OneDriveContent
     * @return OneDriveItem uploaded item
     */
    OneDriveItem upload(OneDriveContent content);

    /**
     * Get ItemAddress
     * 
     * @return ItemAddress
     */
    ItemAddress getAddress();

    /**
     * Get drive resource, never cached because there is not eTag value
     * 
     * @return Drive
     */
    Drive getDrive();

    /**
     * Get User information
     * 
     * @return Identity
     */
    Identity getUser();

    /**
     * Get Quota information
     * 
     * @return QuotaFacet
     */
    QuotaFacet getQuota();

    /**
     * Get drive identifier
     * 
     * @return String
     */
    String getDriveId();

}
