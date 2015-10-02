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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.yucca.microsoft.onedrive.ItemAddress;
import io.yucca.microsoft.onedrive.PathUtil;
import io.yucca.microsoft.onedrive.actions.Addressing;
import io.yucca.microsoft.onedrive.facets.AudioFacet;
import io.yucca.microsoft.onedrive.facets.DeletedFacet;
import io.yucca.microsoft.onedrive.facets.FileFacet;
import io.yucca.microsoft.onedrive.facets.FileSystemInfoFacet;
import io.yucca.microsoft.onedrive.facets.FolderFacet;
import io.yucca.microsoft.onedrive.facets.ImageFacet;
import io.yucca.microsoft.onedrive.facets.LocationFacet;
import io.yucca.microsoft.onedrive.facets.PhotoFacet;
import io.yucca.microsoft.onedrive.facets.SpecialFolderFacet;
import io.yucca.microsoft.onedrive.facets.VideoFacet;

/**
 * The Item resource represents an item stored in OneDrive
 *
 * @author yucca.io
 */
public class Item {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private String eTag;
    @JsonIgnore
    private String cTag;
    @JsonIgnore
    private IdentitySet createdBy;
    @JsonIgnore
    private IdentitySet lastModifiedBy;
    @JsonIgnore
    private String createdDateTime;
    @JsonIgnore
    private String lastModifiedDateTime;
    @JsonIgnore
    private Long size;
    private ItemReference parentReference;
    @JsonIgnore
    private URL webUrl;
    @JsonIgnore
    private FolderFacet folder;
    @JsonIgnore
    private FileFacet file;
    @JsonIgnore
    private FileSystemInfoFacet fileSystemInfo;
    @JsonIgnore
    private ImageFacet image;
    @JsonIgnore
    private PhotoFacet photo;
    @JsonIgnore
    private AudioFacet audio;
    @JsonIgnore
    private VideoFacet video;
    @JsonIgnore
    private LocationFacet location;
    @JsonIgnore
    private DeletedFacet deleted;
    @JsonIgnore
    private SpecialFolderFacet specialFolder;

    // Instance attributes
    @JsonIgnore
    private ConflictBehavior conflictBehavior;
    @JsonIgnore
    private URL downloadUrl;
    @JsonIgnore
    private URL sourceUrl;

    // Relationships
    @JsonIgnore
    private Byte[] content;
    // A collection of the children of this item. Only set if getMetadata is
    // used with expand=true query parameter
    @JsonIgnore
    private List<Item> children;
    @JsonIgnore
    private List<ThumbnailSet> thumbnails;

    // Unknown Properties
    // set only when creating folders
    // @JsonProperty(value = "@odata.context")
    // private URL contextUrl;

    // URL to children. Set if getMetadata is used with expand=true
    // @JsonProperty(value = "children@odata.context")
    // private URL childrenUrl;

    public Item() {
    }

    public Item(String id) {
        this.id = id;
    }

    @JsonIgnore // only read on deserialization
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    // Readable-Writable
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore // only read on deserialization
    public String geteTag() {
        return eTag;
    }

    @JsonProperty
    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    @JsonIgnore // only read on deserialization
    public String getcTag() {
        return cTag;
    }

    @JsonProperty
    public void setcTag(String cTag) {
        this.cTag = cTag;
    }

    @JsonIgnore // only read on deserialization
    public IdentitySet getCreatedBy() {
        return createdBy;
    }

    @JsonProperty
    public void setCreatedBy(IdentitySet createdBy) {
        this.createdBy = createdBy;
    }

    @JsonIgnore // only read on deserialization
    public IdentitySet getLastModifiedBy() {
        return lastModifiedBy;
    }

    @JsonProperty
    public void setLastModifiedBy(IdentitySet lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @JsonIgnore // only read on deserialization
    public String getCreatedDateTime() {
        return createdDateTime;
    }

    @JsonProperty
    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @JsonIgnore // only read on deserialization
    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    @JsonProperty
    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    @JsonIgnore // only read on deserialization
    public Long getSize() {
        return size;
    }

    @JsonProperty
    public void setSize(Long size) {
        this.size = size;
    }

    @JsonProperty
    public ItemReference getParentReference() {
        return parentReference;
    }

    // Readable-Writable
    public void setParentReference(ItemReference parentReference) {
        this.parentReference = parentReference;
    }

    @JsonIgnore // only read on deserialization
    public URL getWebUrl() {
        return webUrl;
    }

    @JsonProperty
    public void setWebUrl(URL webUrl) {
        this.webUrl = webUrl;
    }

    @JsonIgnore // only read on deserialization
    public FolderFacet getFolder() {
        return folder;
    }

    @JsonProperty
    public void setFolder(FolderFacet folder) {
        this.folder = folder;
    }

    @JsonIgnore // only read on deserialization
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

    // Readable-Writable
    public void setFileSystemInfo(FileSystemInfoFacet fileSystemInfo) {
        this.fileSystemInfo = fileSystemInfo;
    }

    @JsonIgnore // only read on deserialization
    public ImageFacet getImage() {
        return image;
    }

    @JsonProperty
    public void setImage(ImageFacet image) {
        this.image = image;
    }

    @JsonIgnore // only read on deserialization
    public PhotoFacet getPhoto() {
        return photo;
    }

    @JsonProperty
    public void setPhoto(PhotoFacet photo) {
        this.photo = photo;
    }

    @JsonIgnore // only read on deserialization
    public AudioFacet getAudio() {
        return audio;
    }

    @JsonProperty
    public void setAudio(AudioFacet audio) {
        this.audio = audio;
    }

    @JsonIgnore // only read on deserialization
    public VideoFacet getVideo() {
        return video;
    }

    @JsonProperty
    public void setVideo(VideoFacet video) {
        this.video = video;
    }

    @JsonIgnore // only read on deserialization
    public LocationFacet getLocation() {
        return location;
    }

    @JsonProperty
    public void setLocation(LocationFacet location) {
        this.location = location;
    }

    @JsonIgnore // only read on deserialization
    public DeletedFacet getDeleted() {
        return deleted;
    }

    @JsonProperty
    public void setDeleted(DeletedFacet deleted) {
        this.deleted = deleted;
    }

    @JsonIgnore // only read on deserialization
    public SpecialFolderFacet getSpecialFolder() {
        return specialFolder;
    }

    @JsonProperty
    public void setSpecialFolder(SpecialFolderFacet specialFolder) {
        this.specialFolder = specialFolder;
    }

    // Instance Attributes
    @JsonProperty(value = "@name.conflictBehavior")
    public ConflictBehavior getConflictBehavior() {
        return conflictBehavior;
    }

    @JsonIgnore // only write-out on serialization
    public void setConflictBehavior(ConflictBehavior conflictBehavior) {
        this.conflictBehavior = conflictBehavior;
    }

    @JsonIgnore // only read on deserialization
    public URL getDownloadUrl() {
        return downloadUrl;
    }

    @JsonProperty(value = "@content.downloadUrl")
    public void setDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @JsonIgnore // only write-out on serialization
    public URL getSourceUrl() {
        return sourceUrl;
    }

    @JsonProperty(value = "@content.sourceUrl")
    public void setSourceUrl(URL sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    // Relationships
    @JsonIgnore // only read on deserialization
    public Byte[] getContent() {
        return content;
    }

    @JsonProperty
    public void setContent(Byte[] content) {
        this.content = content;
    }

    @JsonIgnore // only read on deserialization
    public List<Item> getChildren() {
        return children;
    }

    @JsonProperty // only write-out on serialization
    public void setChildren(List<Item> children) {
        this.children = children;
    }

    @JsonIgnore // only read on deserialization
    public List<ThumbnailSet> getThumbnails() {
        return thumbnails;
    }

    @JsonProperty
    public void setThumbnails(List<ThumbnailSet> thumbnails) {
        this.thumbnails = thumbnails;
    }

    /**
     * Determine if item is a file
     * 
     * @return true if a fileFacet is present
     */
    @JsonIgnore
    public boolean isFile() {
        return (file != null);
    }

    /**
     * Determine if item is a folder
     * 
     * @return true if a folderFacet is present
     */
    @JsonIgnore
    public boolean isDirectory() {
        return (folder != null);
    }

    /**
     * Determine if item is deleted
     * 
     * @return true if a deletedFacet is present
     */
    @JsonIgnore
    public boolean isDeleted() {
        return (deleted != null);
    }

    @JsonIgnore
    public ItemAddress getItemAddress() {
        return new ItemAddress(id, Addressing.ID);
    }

    /**
     * Get absolute path of this item
     * 
     * @return String
     */
    @JsonIgnore
    public String getAbsolutePath() {
        return parentReference.getPath() + "/" + name;
    }

    /**
     * Get path relative to the root folder. "/drive/root:/Folder/file.txt"
     * returns Folder/file.txt
     * 
     * @return String
     */
    @JsonIgnore
    public String getRelativePath() {
        String rel = getParentReference().getPath()
            .replaceAll(PathUtil.DRIVE_ROOT + ":\\/?", "");
        return rel.isEmpty() ? name : rel + "/" + name;
    }

}
