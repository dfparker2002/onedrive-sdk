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
package io.yucca.microsoft.onedrive.resources;

import io.yucca.microsoft.onedrive.facets.QuotaFacet;

/**
 * The Drive resource represents a drive in OneDrive.
 *
 * @author yucca.io
 */
public class Drive {

    private String id;
    private String driveType;
    private IdentitySet owner;
    private QuotaFacet quota;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public IdentitySet getOwner() {
        return owner;
    }

    public void setOwner(IdentitySet owner) {
        this.owner = owner;
    }

    public QuotaFacet getQuota() {
        return quota;
    }

    public void setQuota(QuotaFacet quota) {
        this.quota = quota;
    }

}
