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
import java.text.ParseException;

import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.Item;

/**
 * LocalResourceFactory
 * 
 * @author yucca.io
 */
public class LocalResourceFactory {

    /**
     * Factory method to build an LocalItem (file or folder) based on Item
     * 
     * @param item Item
     * @param content OneDriveContent file contents, {@code null} for folders
     * @param parent LocalResource parent folder or drive
     * @return LocalItem
     * @throws OneDriveException on unknown type of Item has invalid timestamps
     */
    public final static LocalItem build(Item item, OneDriveContent content,
                                        LocalResource parent)
                                            throws IOException {
        try {
            if (item.isFile()) {
                return new LocalFile(item, content, parent);
            } else if (item.isDirectory()) {
                return new LocalFolder(item, parent);
            } else {
                throw new OneDriveException("Unsupported type for item: "
                                            + item.getId() + ", name: "
                                            + item.getName());
            }
        } catch (ParseException e) {
            throw new OneDriveException("Invalid modification timestamp for item: "
                                        + item.getId() + ", name: "
                                        + item.getName());
        }
    }
}
