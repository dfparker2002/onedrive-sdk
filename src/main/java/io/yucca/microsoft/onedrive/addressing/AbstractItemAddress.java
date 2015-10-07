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

import io.yucca.microsoft.onedrive.Addressing;
import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.resources.ItemReference;

/**
 * AbstractItemAddress
 * 
 * @author yucca.io
 */
public abstract class AbstractItemAddress implements ItemAddress {

    protected Addressing method;

    protected String basePath;

    protected String address;

    protected String seperatorStart;

    protected String seperatorEnd;

    protected String addressWithFileName;

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
        if (action != null) {
            base += "/" + action;
        }
        return base;
    }

    @Override
    public String getPathWithAddress() {
        return getPathWithAddress(null);
    }

    @Override
    public String getPathWithAddress(String action) {
        String base = getPath() + seperatorStart + "{item-address}";
        if (action != null) {
            base += seperatorEnd + action;
        }
        return base;
    }

    @Override
    public String getPathWithAddressAndFilename(String action) {
        String base = getPath() + seperatorStart + addressWithFileName;
        if (action != null) {
            base += ":/" + action;
        }
        return base;
    }

    @Override
    public String absolutePath() {
        return getPath() + seperatorStart + address;
    }

    @Override
    public ItemReference getItemReference() {
        ItemReference ref = new ItemReference();
        ref.setPath(absolutePath());
        return ref;
    }

    @Override
    public String toString() {
        return absolutePath();
    }
}
