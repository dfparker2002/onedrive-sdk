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

import io.yucca.microsoft.onedrive.resources.Item;

/**
 * OneDriveItemFactory
 *
 * @author yucca.io
 */
public final class OneDriveItemFactory {

    private OneDriveItemFactory() {
    }

    /**
     * Factory method to build an OneDriveItem type (file or folder)
     * 
     * @param api OneDriveAPIConnection connection to the OneDrive API
     * @param item Item
     * @return OneDriveItem
     */
    public final static OneDriveItemImpl build(OneDriveAPIConnection api,
                                           Item item) {
        if (item.isFile()) {
            return new OneDriveItemImpl(api, item);
        } else if (item.isDirectory()) {
            return new OneDriveFolderImpl(api, item);
        } else {
            throw new OneDriveException("Unsupported type for item : "
                                        + item.getId() + ", name: "
                                        + item.getName());
        }
    }
}
