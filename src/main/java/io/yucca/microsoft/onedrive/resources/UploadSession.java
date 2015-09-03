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

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UploadSession
 *
 * @author yucca.io
 */
public class UploadSession {

    private String uploadUrl; // "https://sn3302.up.1drv.com/up/fe6987415ace7X4e1eF866337",
    private String expirationDateTime; // "2015-01-29T09:21:55.523Z",
    private String[] nextExpectedRanges; // : ["0-"]

    @JsonProperty(value = "@odata.context")
    private URL contextUrl;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public String[] getNextExpectedRanges() {
        return nextExpectedRanges;
    }

    public void setNextExpectedRanges(String[] nextExpectedRanges) {
        this.nextExpectedRanges = nextExpectedRanges;
    }

    public boolean hasSessionExpired() {
        // TODO add date check "2015-01-29T09:21:55.523Z"
        return false;
    }

    public URL getContextUrl() {
        return contextUrl;
    }

    public void setContextUrl(URL contextUrl) {
        this.contextUrl = contextUrl;
    }

}
