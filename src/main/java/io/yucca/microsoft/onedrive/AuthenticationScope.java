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
package io.yucca.microsoft.onedrive;

/**
 * OneDrive Authentication Scopes
 */
public enum AuthenticationScope {

    WL_SIGNIN("wl.signin"),
    WL_OFFLINE_ACCESS("wl.offline_access"),
    ONEDRIVE_READONLY("onedrive.readonly"),
    ONEDRIVE_READWRITE("onedrive.readwrite"),
    ONEDRIVE_APPFOLDER("onedrive.appfolder");

    private String scope;

    /**
     * Constructor
     * 
     * @param scope String
     */
    private AuthenticationScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return String the scope.
     */
    public String getScope() {
        return scope;
    }

    /**
     * Create space seperated scope line
     * 
     * @param AuthenticationScope[] scopes
     * @return String
     */
    static String getScopes(AuthenticationScope[] scopes) {
        StringBuilder b = new StringBuilder();
        for (AuthenticationScope s : scopes) {
            b.append(s.getScope()).append(" ");
        }
        return b.toString().trim();
    }
}
