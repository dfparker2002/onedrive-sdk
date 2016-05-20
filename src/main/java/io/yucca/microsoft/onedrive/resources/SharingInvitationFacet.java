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

/**
 * Represents information about a sharing invitation for a set of permissions.
 * 
 * @author yucca.io
 */
public class SharingInvitationFacet {

    private String email;
    private Boolean signInRequired;
    private IdentitySet invitedBy;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSignInRequired() {
        return signInRequired;
    }

    public void setSignInRequired(Boolean signInRequired) {
        this.signInRequired = signInRequired;
    }

    public IdentitySet getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(IdentitySet invitedBy) {
        this.invitedBy = invitedBy;
    }

}
