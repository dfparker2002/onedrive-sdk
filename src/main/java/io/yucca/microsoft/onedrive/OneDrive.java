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
import io.yucca.microsoft.onedrive.resources.QuotaFacet;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * OneDrive represents a Drive in an OneDrive Personal or Business.
 *
 * @author yucca.io
 */
public interface OneDrive {

    /**
     * Create a new folder inside this drive.
     * 
     * @param name String name of the folder
     * @param behaviour ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then default to {@link ConflictBehavior#FAIL}
     * @return OneDriveFolder created folder
     */
    OneDriveFolder createFolder(String name, ConflictBehavior behaviour);

    /**
     * Create a new folder inside this drive, if the folder already exists the
     * creation fails.
     * 
     * @param name String name of the folder
     * @return OneDriveFolder created folder
     */
    OneDriveFolder createFolder(String name);

    /**
     * Get the root folder of this drive.
     * 
     * @return OneDriveFolder
     */
    OneDriveFolder getRootFolder();

    /**
     * Get a special folder in this drive.
     * 
     * @param folder SpecialFolder
     * @param parameters QueryParameters optional parameter to influence the way
     *            the results are returned
     * @return OneDriveFolder
     */
    OneDriveFolder getSpecialFolder(SpecialFolder folder,
                                    QueryParameters parameters);

    /**
     * Get a folder by address.
     * 
     * @param address ItemAddress address of item
     * @return OneDriveFolder
     */
    OneDriveFolder getFolder(ItemAddress address);

    /**
     * Get a folder by path.
     * 
     * @param path String path to a folder relative to the drive root i.e.
     *            "Documents"
     * @return OneDriveFolder
     */
    OneDriveFolder getFolder(String path);

    /**
     * Get an item by address.
     * 
     * @param address ItemAddress address of item
     * @return OneDriveItem
     */
    OneDriveItem getItem(ItemAddress address);

    /**
     * Get an item by path.
     * 
     * @param path String path to item relative to the drive root
     * @return OneDriveItem
     */
    OneDriveItem getItem(String path);

    /**
     * Get all children in this drive.
     * 
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem>
     */
    Collection<OneDriveItem> listChildren(QueryParameters parameters);

    /**
     * Get all children in this drive.
     * 
     * @return Collection<OneDriveItem>
     */
    Collection<OneDriveItem> listChildren();

    /**
     * Search for items in this drive matching the query.
     * 
     * @param query String search query
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem> results
     */
    List<OneDriveItem> search(String query, QueryParameters parameters);

    /**
     * Upload the content into the drive root.
     * 
     * @param content OneDriveContent
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveItem uploaded item
     */
    OneDriveItem upload(OneDriveContent content, ConflictBehavior behavior);

    /**
     * Upload the content into the drive root, if the file already exists
     * uploading fails.
     * 
     * @param content OneDriveContent
     * @return OneDriveItem uploaded item
     */
    OneDriveItem upload(OneDriveContent content);

    /**
     * Get the address for the drive.
     * 
     * @return ItemAddress
     */
    ItemAddress getAddress();

    /**
     * Get the drive resource and cache it. This will become stale over time,
     * because no eTag value if available to detect modifications of the entity.
     * 
     * @return Drive
     */
    Drive getDrive();

    /**
     * Get the available drives.
     * 
     * @return List<Drive>
     */
    List<Drive> getDrives();

    /**
     * Get user information.
     * 
     * @return Identity
     */
    Identity getUser();

    /**
     * Get quota information.
     * 
     * @return QuotaFacet
     */
    QuotaFacet getQuota();

    /**
     * Get drive identifier.
     * 
     * @return String
     */
    String getDriveId();

}
