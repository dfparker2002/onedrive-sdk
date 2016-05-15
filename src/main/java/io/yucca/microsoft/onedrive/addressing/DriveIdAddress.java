/**
 * Copyright 2016 Rob Sessink
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
 * DriveIdAddress, addressing of items by drive and item identifier
 * 
 * @author yucca.io
 */
public class DriveIdAddress extends AbstractItemAddress {

    protected final String driveId;

    /**
     * Construct an address for an item within a specific drive
     * 
     * @param address String item identifier
     * @param driveId String drive identifier
     */
    public DriveIdAddress(String address, String driveId) {
        super(address);
        this.driveId = driveId;
        init();
    }

    /**
     * Construct an address for an item within a specific drive
     * 
     * @param item Item
     */
    public DriveIdAddress(Item item) {
        super(item.getId());
        this.driveId = item.getParentReference().getDriveId();
        init();
    }

    private void init() {
        this.method = Addressing.ID;
        this.seperatorStart = "/";
        this.seperatorEnd = "/";
        this.basePath = DRIVES + "/" + driveId + "/" + ITEMS;
        this.addressWithFileName = ITEM_ADDRESS + "/" + FILENAME;
    }

    /**
     * @return Returns the driveId.
     */
    public String getDriveId() {
        return driveId;
    }

}
