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
 * RootAddress, addressing of items in root folder by path
 * 
 * @author yucca.io
 */
public class RootAddress extends AbstractItemAddress {

    /**
     * Constructor address that corresponds with the root folder i.e.
     * "/drive/root:"
     */
    public RootAddress() {
        super("/");
        this.method = Addressing.ROOT;
        this.seperatorStart = ":";
        this.seperatorEnd = ":/";
        this.basePath = DRIVE_ROOT;
        this.addressWithFileName = "{item-address}{filename}";
    }
}
