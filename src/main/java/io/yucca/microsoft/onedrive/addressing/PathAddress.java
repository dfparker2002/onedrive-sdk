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
package io.yucca.microsoft.onedrive.addressing;

import io.yucca.microsoft.onedrive.resources.Item;

/**
 * PathAddress, addressing of items by path
 * 
 * @author yucca.io
 */
public class PathAddress extends AbstractItemAddress {

    /**
     * Construct a path address
     * 
     * @param address String path relative to the root folder i.e.
     *            "/drive/root:"
     */
    public PathAddress(String address) {
        super("/" + address);
        init();
    }

    /**
     * Construct an address for Item
     * 
     * @param item Item
     */
    public PathAddress(Item item) {
        super("/" + item.getRelativePath());
        init();
    }

    /**
     * Construct an address relative to Item
     * 
     * @param item Item
     * @param address String path relative to the Item path (i.e. a child item)
     */
    public PathAddress(Item item, String address) {
        super("/" + item.getRelativePath() + "/" + address);
        init();
    }

    private void init() {
        this.method = Addressing.PATH;
        this.seperatorStart = ":";
        this.seperatorEnd = ":/";
        this.basePath = DRIVE_ROOT;
        this.addressWithFileName = ITEM_ADDRESS + "/" + FILENAME;
    }

}
