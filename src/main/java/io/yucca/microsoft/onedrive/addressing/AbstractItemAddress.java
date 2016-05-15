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
 * AbstractItemAddress
 * 
 * @author yucca.io
 */
public abstract class AbstractItemAddress implements ItemAddress {

    static final String DRIVE_ROOT = "/drive/root";

    static final String DRIVE_ITEMS = "/drive/items";

    static final String DRIVE_SPECIAL = "/drive/special";

    static final String DRIVE = "/drive";

    static final String DRIVES = "/drives";

    static final String ITEMS = "items";

    static final String ITEM_ADDRESS = "{item-address}";

    protected static final String FILENAME = "{filename}";

    protected Addressing method;

    protected String basePath;

    protected String address;

    protected String seperatorStart;

    protected String seperatorEnd;

    protected String addressWithFileName;

    AbstractItemAddress() {
        this.address = "";
    }
    
    AbstractItemAddress(String address) {
        this.address = address;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPath() {
        return getPath(null);
    }

    @Override
    public String getPath(String action) {
        String base = basePath;
        return (action != null) ? base += "/" + action : base;
    }

    @Override
    public String getPathWithAddress() {
        return getPathWithAddress(null);
    }

    @Override
    public String getPathWithAddress(String action) {
        String base = getPath() + seperatorStart + ITEM_ADDRESS;
        return (action != null) ? base += seperatorEnd + action : base;
    }

    @Override
    public String getPathWithAddress(String action, String parameter) {
        String base = getPathWithAddress(action);
        return (parameter != null) ? base += "/" + parameter : base;
    }

    @Override
    public String getPathWithAddressAndFilename(String action) {
        String base = getPath() + seperatorStart + addressWithFileName;
        return (action != null) ? base += ":/" + action : base;
    }

    @Override
    public String absolutePath() {
        return (address == null || address.isEmpty())
            ? getPath() : getPath() + seperatorStart + address;
    }

    @Override
    public String toString() {
        return absolutePath();
    }
}
