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
 * The itemReference type groups data needed to reference a OneDrive item across
 * the service into a single structure.
 * 
 * @author yucca.io
 */
public class ItemReference {

    private String driveId;
    private String id;
    private String path;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    /**
     * Set the path
     * 
     * @param path String path to parent folder relative to the root folder i.e.
     *            "/drive/root:/". If null/empty the root folder is assumed.
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        if (id != null) {
            s.append("id: ").append(this.id);
        }
        if (s.length() != 0) {
            s.append(", ");
        }
        if (path != null) {
            s.append(" path: ").append(this.path);
        }
        return s.toString();
    }

}
