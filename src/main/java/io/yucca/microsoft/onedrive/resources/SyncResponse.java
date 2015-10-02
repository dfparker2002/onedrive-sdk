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
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SyncResponse, the resource includes a collection of items that have changes
 * and information about how to retrieve the next set of changes.
 *
 * @author yucca.io
 */
public class SyncResponse extends ItemCollection {

    @JsonProperty("@odata.deltaLink")
    private URL deltaLink;

    @JsonProperty("@delta.token")
    private String token;

    public void setDeltaLink(URL deltaLink) {
        this.deltaLink = deltaLink;
    }

    public URL getDeltaLink() {
        return deltaLink;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Convert the syncResponse to a LinkedHashMap where items are identified by
     * Id
     * 
     * @return LinkedHashMap<String, Item>
     */
    public Map<String, Item> asMap() {
        Map<String, Item> deltaMap = new LinkedHashMap<>();
        for (Item item : this) {
            deltaMap.put(item.getId(), item);
        }
        return deltaMap;
    }
}
