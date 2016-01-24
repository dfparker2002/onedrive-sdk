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
 * SharingLinkFacet
 *
 * @author yucca.io
 */
public class SharingLinkFacet {

    private String token;
    private String webUrl;
    private LinkType type;
    private Identity application;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }

    public Identity getApplication() {
        return application;
    }

    public void setApplication(Identity application) {
        this.application = application;
    }

}
