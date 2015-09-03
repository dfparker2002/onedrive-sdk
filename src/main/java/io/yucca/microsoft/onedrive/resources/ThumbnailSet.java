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
 * ThumbnailSet
 * 
 * @author yucca.io
 */
public class ThumbnailSet {

    private String id;
    private Thumbnail small;
    private Thumbnail medium;
    private Thumbnail large;
    private Thumbnail customThumbnails;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Thumbnail getSmall() {
        return small;
    }

    public void setSmall(Thumbnail small) {
        this.small = small;
    }

    public Thumbnail getMedium() {
        return medium;
    }

    public void setMedium(Thumbnail medium) {
        this.medium = medium;
    }

    public Thumbnail getLarge() {
        return large;
    }

    public void setLarge(Thumbnail large) {
        this.large = large;
    }

    public Thumbnail getCustomThumbnails() {
        return customThumbnails;
    }

    public void setCustomThumbnails(Thumbnail customThumbnails) {
        this.customThumbnails = customThumbnails;
    }

}
