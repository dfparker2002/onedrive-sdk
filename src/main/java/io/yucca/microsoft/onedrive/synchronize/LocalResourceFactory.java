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

import java.io.IOException;
import java.nio.file.Path;

import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.actions.DownloadAction;
import io.yucca.microsoft.onedrive.addressing.IdAddress;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.ItemReference;

/**
 * LocalResourceFactory
 * 
 * @author yucca.io
 */
public final class LocalResourceFactory {

    private LocalResourceFactory() {
    }

    /**
     * Factory method to build an LocalItem (file or folder) based on Item
     * 
     * @param item Item from OneDrive
     * @param api OneDriveAPIConnection connection to OneDrive API
     * @param repository LocalDriveRepository to local repository
     * @return LocalItem instantiated item
     * @throws OneDriveException on unknown type
     */
    public static final LocalItem newInstance(Item item,
                                              OneDriveAPIConnection api,
                                              LocalDriveSynchronizer repository)
                                                  throws IOException {
        Path path = itemPath(item, repository);
        if (item.isFile()) {
            OneDriveContent content = download(item, api);
            return new LocalFileImpl(path, item, content, repository);
        } else if (item.isDirectory()) {
            return new LocalFolderImpl(path, item, repository);
        } else {
            throw new OneDriveException("Unsupported type for item: "
                                        + item.getId() + ", name: "
                                        + item.getName());
        }
    }

    /**
     * Get the local path for an Item.
     * <p>
     * The local parent folder is acquired over a lookup with the Id field of
     * {@link Item#getParentReference()}. For the lookup to succeed the folder
     * must be registered under Id, either as a manual call to
     * {@link #registerFolder(LocalFolder)} or via {@link #walkPath()}
     * </p>
     * 
     * @param item Item
     * @return Path
     */
    private static Path itemPath(Item item, LocalDriveSynchronizer repository) {
        ItemReference parentRef = item.getParentReference();
        if (parentRef == null) {
            throw new OneDriveException("Item: " + item
                                        + " has no ParentReference, cannot lookup parent.");
        }
        LocalFolder parent = repository.getLocalFolder(parentRef.getId());
        return parent.resolve(item.getName());
    }

    private static OneDriveContent download(Item item,
                                            OneDriveAPIConnection api) {
        ItemAddress address = new IdAddress(item.getId());
        return new DownloadAction(api, address).call();
    }

}
