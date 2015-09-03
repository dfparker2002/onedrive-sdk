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
package io.yucca.microsoft.onedrive.synchronize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;

import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.io.InputStreamingOutput;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.util.ChecksumUtil;
import io.yucca.microsoft.onedrive.util.ISO8061;

/**
 * Represents a file on the local filesystem
 *
 * @author yucca.io
 */
public class LocalFile implements LocalItem {

    private static final long serialVersionUID = 1584688343230415773L;

    private static final int PRECISION_MS = 1000;

    protected transient Path path;

    protected String name;

    protected String id;

    private transient FileTime createdDateTime;

    private transient FileTime lastModifiedDateTime;

    /**
     * Construct a LocalFile based on path, if the file exists the OneDrive
     * metadata is read from disk
     * 
     * @param file
     * @throws IOException if reading metadata fails
     */
    public LocalFile(Path file) throws IOException {
        this.name = file.getFileName().toString();
        this.path = file;
        if (exists()) {
            readMetadata();
        }
    }

    /**
     * Construct a LocalFile based on Item to be stored in LocalFolder. The
     * OneDrive metadata is provided by the item
     * 
     * @param item Item
     * @param parent LocalFolder
     * @throws IOException if resolving parent fails
     * @throws ParseException if parsing of last modification of timestamp fails
     */
    public LocalFile(Item item, LocalResource parent)
        throws IOException, ParseException {
        this.id = item.getId().toUpperCase();
        this.name = item.getName();
        this.path = parent.getPath().resolve(name);
        this.lastModifiedDateTime = fromISO8601(item.getLastModifiedDateTime());
        parent.resetTimestamps();
    }

    /**
     * Construct a LocalFile based on Item to be stored in LocalFolder. The
     * OneDrive metadata is provided by the item.
     * 
     * @param item Item
     * @param content OneDriveContent
     * @param parent LocalResource
     * @throws IOException if writing content or metadata fails
     * @throws ParseException if parsing of last modification of timestamp fails
     */
    public LocalFile(Item item, OneDriveContent content, LocalResource parent)
        throws IOException, ParseException {
        this.id = item.getId().toUpperCase();
        this.name = item.getName();
        this.path = parent.getPath().resolve(name);
        this.lastModifiedDateTime = fromISO8601(item.getLastModifiedDateTime());
        writeMetadataAndContent(content);
        parent.resetTimestamps();
    }

    /**
     * Reset the timestamps of a resource based on field values. When a file is
     * created in a folder the underlying filesystem will update the timestamp
     * by which they are not in sync anymore with the OneDrive timestamp.
     * 
     * @throws IOException
     */
    public void resetTimestamps() throws IOException {
        if (ResourceType.FOLDER.equals(type())) {
            Files.setLastModifiedTime(path, lastModifiedDateTime);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    /**
     * Updates the metadata attributes and content of the local file. If the
     * item name was changed, the file is renamed
     * 
     * @param item Item
     * @param content OneDriveContent
     * @param parent LocalResource
     * @throws IOException
     */
    @Override
    public void update(Item item, OneDriveContent content, LocalResource parent)
        throws IOException {
        try {
            rename(item.getName());
            relateWith(item);
            writeMetadataAndContent(content);
            parent.resetTimestamps();
        } catch (ParseException e) {
            throw new OneDriveException("Failed to parse timestamp: "
                                        + item.getLastModifiedDateTime(), e);
        }
    }

    /**
     * Relate this local item with the OneDrive item, setting metadata
     * properties
     * 
     * @param item Item
     * @throws ParseException if timestamps fail to parse
     */
    protected void relateWith(Item item) throws ParseException {
        this.id = item.getId();
        this.name = item.getName();
        this.createdDateTime = fromISO8601(item.getCreatedDateTime());
        this.lastModifiedDateTime = fromISO8601(item.getLastModifiedDateTime());
    }

    private void writeMetadataAndContent(OneDriveContent content)
        throws IOException {
        if (content != null) {
            write(content);
        }
        writeMetadata();
    }

    private void write(OneDriveContent content) throws IOException {
        try (
            OutputStream out = Files.newOutputStream(path,
                                                     StandardOpenOption.CREATE,
                                                     StandardOpenOption.TRUNCATE_EXISTING);
            InputStreamingOutput iso = new InputStreamingOutput(content
                .getInputStream())) {
            iso.write(out);
        }
    }

    /**
     * Rename the file, usage of StandardCopyOption.ATOMIC_MOVE,
     * StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING
     * is not supported on Linux
     * 
     * @param name String
     * @throws IOException
     */
    @Override
    public void rename(String name) throws IOException {
        if (isRenamed(name)) {
            this.name = name;
            this.path = Files.move(path, path.resolveSibling(name),
                                   new CopyOption[] {});
        }
    }

    @Override
    public void delete() throws IOException {
        Files.delete(path);
    }

    /**
     * Compare modification date of this file in regard to lastModifiedDateTime.
     * If {@link Item#getLastModifiedDateTime() cannot be parsed
     * ModificationStatus.NOTMODIFIED is returned. <p> When metadata timestamps
     * like lastModifiedDateTime are witten the precision is dependant on the
     * underlying filesystem. Linux/ext4fs looses the milliseconds precision,
     * therefor modification status is done on second precision</p>
     * 
     * @param item Item
     * @return ModificationStatus
     */
    @Override
    public ModificationStatus lastModificationStatus(Item item) {
        try {
            long itemLastModified = ISO8061
                .toCalendar(item.getLastModifiedDateTime()).getTimeInMillis();
            if ((itemLastModified
                 - lastModifiedDateTime.toMillis()) > PRECISION_MS) {
                return ModificationStatus.OLDER;
            } else if ((lastModifiedDateTime.toMillis()
                        - itemLastModified) > PRECISION_MS) {
                return ModificationStatus.NEWER;
            } else {
                return ModificationStatus.NOTMODIFIED;
            }
        } catch (ParseException e) {
            return ModificationStatus.NOTMODIFIED;
        }
    }

    /**
     * Determine if the file has been modified with regards to the Item, this is
     * done by comparing the sha1 hashes of the file and the item
     */
    @Override
    public boolean isContentModified(Item item) throws IOException {
        String sha1 = item.getFile().getHashes().getSha1Hash();
        return (sha1.equalsIgnoreCase(ChecksumUtil.sha1(path)) == false);
    }

    protected boolean isRenamed(String name) {
        return (path.getFileName().toString().equals(name) == false);
    }

    /**
     * Read metadata attributes from the LocalFile
     * 
     * @throws IOException
     */
    protected void readMetadata() throws IOException {
        BasicFileAttributeView basicView = Files
            .getFileAttributeView(path, BasicFileAttributeView.class);
        BasicFileAttributes basicAttrs = basicView.readAttributes();
        this.createdDateTime = FileTime
            .fromMillis(basicAttrs.creationTime().toMillis());
        this.lastModifiedDateTime = FileTime
            .fromMillis(basicAttrs.lastModifiedTime().toMillis());
        this.id = MetadataUtil.readAttribute(path, ATTRIBUTE_ONEDRIVE_ITEMID);
    }

    /**
     * When writing timestamp like lastModifiedDateTime the precision is
     * dependant on the underlying filesystem (Linux/ext4fs) looses the
     * milliseconds precision, therefor changed det
     * 
     * @throws IOException
     */
    protected void writeMetadata() throws IOException {
        Files.setLastModifiedTime(path, lastModifiedDateTime);
        MetadataUtil.writeAttribute(path, ATTRIBUTE_ONEDRIVE_ITEMID, id);
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Derive parentId from the LocalFolder in which this file is stored
     * 
     * @return String
     * @throws IOException
     */
    @Override
    public String getParentId() throws IOException {
        return new LocalFolder(path.getParent()).getId();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ResourceType type() {
        return ResourceType.FILE;
    }

    @Override
    public boolean hasId() {
        return (this.id != null);
    }

    @Override
    public void updateItem(Item item) {
        item.setName(name);
        item.getFileSystemInfo().setCreatedDateTime(createdDateTime.toString());
        item.getFileSystemInfo()
            .setLastModifiedDateTime(lastModifiedDateTime.toString());
    }

    /**
     * Get a contents of this file
     * 
     * @return OneDriveFile content
     * @throws FileNotFoundException
     */
    public OneDriveFile getOneDriveContent() throws FileNotFoundException {
        return new OneDriveFile(path);
    }

    private FileTime fromISO8601(String iso8601) throws ParseException {
        return FileTime.fromMillis(ISO8061.toMS(iso8601));
    }

    public String toString() {
        return "LocalFile: " + getPath() + " id: " + getId();
    }

}
