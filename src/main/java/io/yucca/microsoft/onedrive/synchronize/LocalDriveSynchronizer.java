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
package io.yucca.microsoft.onedrive.synchronize;

import java.util.List;

import io.yucca.microsoft.onedrive.OneDriveException;

/**
 * LocalDriveSynchronizer
 * 
 * @author yucca.io
 */
public interface LocalDriveSynchronizer extends LocalDriveRepository {

    /**
     * Initialize a synchronization session for a folder. This session could be
     * based on the state of a existing session state.
     * 
     * @param useSavedState boolean false start with a empty session state (used
     *            for full synchronizations), if false try to deserialize the
     *            existing session state (incremental synchronizations).
     * @param folder LocalFolder to be synced
     */
    void initializeSession(boolean useSavedState, LocalFolder folder);

    /**
     * Clear the state of this synchronization session, must be called when the
     * synchronization has failed or is restarted.
     */
    void clearSession();

    /**
     * Save the state of this synchronization session, serializing the state to
     * disk, on future sessions this is reused.
     */
    void saveSession();

    /**
     * Register a LocalItem as new addition in the LocalDrive
     * 
     * @param item LocalItem
     */
    void registerAddition(LocalItem item);

    /**
     * Register a LocalItem for presence in the LocalDrive
     * 
     * @param item LocalItem
     */
    void registerItem(LocalItem item);

    /**
     * Deregister a LocalItem from the LocalDrive
     * 
     * @param item LocalItem
     */
    void deregisterItem(LocalItem item);

    /**
     * Register a LocalItem as deleted in the LocalDrive
     * 
     * @param item LocalFile
     */
    void registerDeletion(LocalItem item);

    /**
     * Register a folder or drive for lookup
     * 
     * @param folder LocalFolder
     */
    void registerFolder(LocalFolder folder);

    /**
     * Get all additions made to the LocalDrive since the previous saving of the
     * LocalDrive state
     * 
     * @return List<LocalItem>
     */
    List<LocalItem> getAdditions();

    /**
     * Get all deletions made in the LocalDrive since the previous saving of the
     * LocalDrive state
     * 
     * @return List<LocalItem>
     */
    List<LocalItem> getDeletions();

    /**
     * Get a folder identified by id
     * 
     * @param id String
     * @return LocalFolder
     * @throws OneDriveException if no folder is available by id
     */
    LocalFolder getLocalFolder(String id);

    /**
     * Get item identified by id
     * 
     * @param id String
     * @return LocalItem
     * @throws OneDriveException if no folder available by id
     */
    LocalItem getLocalItem(String id);

}
