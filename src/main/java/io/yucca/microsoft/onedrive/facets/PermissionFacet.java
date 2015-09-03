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
package io.yucca.microsoft.onedrive.facets;

import io.yucca.microsoft.onedrive.resources.ItemReference;

/**
 * PermissionFacet
 *
 * @author yucca.io
 */
public class PermissionFacet {

    private String id;
    private String[] role;
    private SharingLinkFacet link;
    private ItemReference inheritedFrom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }

    public SharingLinkFacet getLink() {
        return link;
    }

    public void setLink(SharingLinkFacet link) {
        this.link = link;
    }

    public ItemReference getInheritedFrom() {
        return inheritedFrom;
    }

    public void setInheritedFrom(ItemReference inheritedFrom) {
        this.inheritedFrom = inheritedFrom;
    }

}
