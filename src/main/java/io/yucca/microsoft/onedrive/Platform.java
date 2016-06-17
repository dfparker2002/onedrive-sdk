/**
 * Copyright 2016 Rob Sessink
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
 * Enum defining the platforms that can be accessed through the OneDrive API
 *
 * @author yucca.io
 */
public enum Platform {

    PERSONAL("OneDrive Personal", "https://api.onedrive.com/v1.0"),
    BUSINESS(
        "OneDrive Business",
        "https://{tenant}-my.sharepoint.com/_api/v2.0"),
    SHAREPOINTONLINE(
        "SharePoint Online",
        "https://{tenant}.sharepoint.com/{site-relative-path}/_api/v2.0");

    private final String name;

    private final String url;

    /**
     * Constructor
     * 
     * @param String platform
     */
    private Platform(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Create Platform by name
     * 
     * @param name String
     * @return Platform
     */
    public static Platform create(String name) {
        for (Platform p : values()) {
            if (p.name().equals(name)) {
                return p;
            }
        }
        throw new IllegalArgumentException();
    }
}
