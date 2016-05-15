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

/**
 * IdAddress, addressing of shared items by driveId
 * 
 * @author yucca.io
 */
public class SharedAddress extends DriveIdAddress {

    public static final String SHARED = "shared";

    /**
     * Constructor
     */
    public SharedAddress() {
        super("", "");
        init();
    }

    /**
     * Constructor
     * 
     * @param driveId String drive identifier
     */
    public SharedAddress(String driveId) {
        super("", driveId);
        init();
    }

    private void init() {
        this.method = Addressing.ID;
        this.seperatorStart = "";
        this.seperatorEnd = "";
        this.basePath = (driveId == null || driveId.isEmpty())
            ? DRIVE + "/" + SHARED : DRIVES + "/" + driveId + "/" + SHARED;
        this.addressWithFileName = "";
    }

}