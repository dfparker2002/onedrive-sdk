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
 * IdAddress, addressing of items by identifier
 * 
 * @author yucca.io
 */
public class IdAddress extends AbstractItemAddress {

    /**
     * Constructor
     * 
     * @param address String item identifier
     */
    public IdAddress(String address) {
        super(address);
        this.basePath = DRIVE_ITEMS;
        init();
    }

    /**
     * Construct an address for an item within a specific drive
     * 
     * @param address String item identifier
     * @param driveId String drive identifier
     */
    public IdAddress(String address, String driveId) {
        super(address);
        this.basePath = DRIVES + "/" + driveId + "/items";
        init();
    }

    /**
     * Construct an address for Item
     * 
     * @param item Item
     */
    public IdAddress(Item item) {
        super(item.getId());
        this.basePath = DRIVE_ITEMS;
        init();
    }

    private void init() {
        this.method = Addressing.ID;
        this.seperatorStart = "/";
        this.seperatorEnd = "/";
        this.addressWithFileName = "{item-address}:/{filename}";
        this.basePath = DRIVE_ITEMS;
    }
}
