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

import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

/**
 * OneDriveFolder represent a folder within a OneDrive in which items like
 * audio, files, images, videos or others folders are stored.
 *
 * @author yucca.io
 */
public interface OneDriveFolder extends OneDriveItem {

    /**
     * Copy this folder recursively to the destination folder
     * 
     * @param destination OneDriveFolder
     * @param name String name of the new folder, if {@code null} the same name
     *            is used
     * @return OneDriveFolder copied folder
     */
    OneDriveFolder copy(OneDriveFolderImpl destination, String name);

    /**
     * Create a new folder inside this folder
     * 
     * @param name String name of the folder @param behaviour ConflictBehavior
     *            behaviour if a naming conflict occurs, if {@code null} then
     *            defaults to {@link ConflictBehavior#FAIL}
     * @param behavior ConflictBehavior behaviour if a naming conflict occurs,
     *            if {@code null} then defaults to {@link ConflictBehavior#FAIL}
     * @return OneDriveFolder created folder
     */
    OneDriveFolder createFolder(String name, ConflictBehavior behavior);

    /**
     * Create a new folder inside this folder, if the folder already exists
     * creation fails
     * 
     * @param name String name of the folder
     * @return OneDriveFolder created folder
     */
    OneDriveFolder createFolder(String name);

    /**
     * Get a folder located inside this folder
     * 
     * @param path String name or path of folder, relative to this folder
     * @return OneDriveFolder
     */
    OneDriveFolder getFolder(String path);

    /**
     * Get an item inside this folder
     * 
     * @param path String name or path of item, relative to this folder
     * @return OneDriveItem
     */
    OneDriveItem getItem(String path);

    /**
     * Get all children in this folder
     * 
     * @param parameters QueryParameters optional parameter to influence the way
     *            the result is returned
     * @return Collection<OneDriveItem>
     */
    Collection<OneDriveItemImpl> listChildren(QueryParameters parameters);

    /**
     * Delete this folder and (recursively) all the children contents
     */
    void delete();

    /**
     * Download of a folder is unsupported
     * 
     * @throws UnsupportedOperationException
     */
    OneDriveContent download();

    /**
     * Move this folder to destination
     * 
     * @param destination OneDriveFolder
     * @return Item moved item
     */
    OneDriveFolder move(OneDriveFolderImpl destination);

    /**
     * Move this folder to special folder
     * 
     * @param destination SpecialFolder
     * @return Item moved item
     */
    OneDriveFolder move(SpecialFolder destination);

    /**
     * Move this folder to root of Drive
     * 
     * @param destination OneDrive
     * @return Item moved item
     */
    OneDriveFolder move(OneDrive destination);

    /**
     * Rename the folder
     * 
     * @param name String new name
     */
    void rename(String name);

    /**
     * Search for items in this folder matching the query
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

}
