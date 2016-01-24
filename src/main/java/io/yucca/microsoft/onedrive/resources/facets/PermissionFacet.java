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
package io.yucca.microsoft.onedrive.resources.facets;

import io.yucca.microsoft.onedrive.resources.IdentitySet;
import io.yucca.microsoft.onedrive.resources.ItemReference;
import io.yucca.microsoft.onedrive.resources.Role;

/**
 * PermissionFacet
 * 
 * @author yucca.io
 */
public class PermissionFacet {

    private String id;
    private Role[] roles;
    private SharingLinkFacet link;
    private IdentitySet grantedTo;
    private ItemReference inheritedFrom;
    private String shareId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    public SharingLinkFacet getLink() {
        return link;
    }

    public void setLink(SharingLinkFacet link) {
        this.link = link;
    }

    public IdentitySet getGrantedTo() {
        return grantedTo;
    }

    public void setGrantedTo(IdentitySet grantedTo) {
        this.grantedTo = grantedTo;
    }

    public ItemReference getInheritedFrom() {
        return inheritedFrom;
    }

    public void setInheritedFrom(ItemReference inheritedFrom) {
        this.inheritedFrom = inheritedFrom;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

}
