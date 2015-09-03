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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * SpecialFolder
 *
 * @author yucca.io
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SpecialFolder {

    DOCUMENTS("documents", "Documents"),
    PHOTOS("photos", "Photos"),
    MUSIC("music", "Music"),
    CAMERAROLL("cameraroll", "Camera Roll"),
    APPROOT("approot", "App Root"),
    PUBLIC("public", "Public");

    public static final String SPECIAL_FOLDER_PATH = "/drive/special/";

    private String id;

    private String name;

    private SpecialFolder(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return SPECIAL_FOLDER_PATH + name;
    }

    @JsonCreator
    public static SpecialFolder create(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        for (SpecialFolder v : values()) {
            if (v.getId().equals(name)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }
}
