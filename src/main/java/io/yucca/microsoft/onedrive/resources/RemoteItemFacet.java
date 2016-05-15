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
package io.yucca.microsoft.onedrive.resources;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RemoteItemFacet
 *
 * @author yucca.io
 */
public class RemoteItemFacet {

    @JsonIgnore
    private String id;
    private ItemReference parentReference;
    @JsonIgnore
    private FolderFacet folder;
    @JsonIgnore
    private FileFacet file;
    @JsonIgnore
    private FileSystemInfoFacet fileSystemInfo;
    @JsonIgnore
    private Long size;
    @JsonIgnore
    private URL webUrl;

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public ItemReference getParentReference() {
        return parentReference;
    }

    @JsonProperty
    public void setParentReference(ItemReference parentReference) {
        this.parentReference = parentReference;
    }

    @JsonIgnore
    public FolderFacet getFolder() {
        return folder;
    }

    @JsonProperty
    public void setFolder(FolderFacet folder) {
        this.folder = folder;
    }

    @JsonIgnore
    public FileFacet getFile() {
        return file;
    }

    @JsonProperty
    public void setFile(FileFacet file) {
        this.file = file;
    }

    @JsonProperty
    public FileSystemInfoFacet getFileSystemInfo() {
        return fileSystemInfo;
    }

    @JsonProperty
    public void setFileSystemInfo(FileSystemInfoFacet fileSystemInfo) {
        this.fileSystemInfo = fileSystemInfo;
    }

    @JsonIgnore
    public Long getSize() {
        return size;
    }

    @JsonProperty
    public void setSize(Long size) {
        this.size = size;
    }

    public URL getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(URL webUrl) {
        this.webUrl = webUrl;
    }

    public Map<String, String> asMap() {
        Map<String, String> m = new HashMap<>();
        m.put("id", id);
        m.put(ItemReference.PARENT_REFERENCE, parentReference.getDriveId());
        return m;
    }

}
