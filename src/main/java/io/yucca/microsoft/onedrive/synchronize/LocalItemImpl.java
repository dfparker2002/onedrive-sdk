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

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.util.ISO8061;

/**
 * LocalItemImpl
 * 
 * @author yucca.io
 */
public abstract class LocalItemImpl implements LocalItem {

    private static final long serialVersionUID = -648879311169333808L;

    private static final int PRECISION_MS = 1000;

    protected transient Path path;

    protected String name;

    protected String id;

    private long createdDateTime;

    private long lastModifiedDateTime;

    protected transient LocalDriveRepository repository;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void create(LocalItem resource) throws IOException {
        repository.create(resource);
    }

    @Override
    public void delete() throws IOException {
        repository.delete(this);
    }

    @Override
    public boolean exists() {
        return repository.exists(this);
    }

    @Override
    public void rename(String name) throws IOException {
        repository.rename(this, name);
    }

    @Override
    public void update(Item item) throws IOException {
        relateWith(item);
        repository.update(this, null);
    }

    @Override
    public String getParentId() throws IOException {
        return new LocalFolderImpl(path.getParent(), repository).getId();
    }

    @Override
    public LocalFolder getParent() throws IOException {
        return new LocalFolderImpl(path.getParent(), repository);
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
    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public ResourceType type() {
        return ResourceType.FILE;
    }

    @Override
    public boolean hasId() {
        return this.id != null;
    }

    public String toString() {
        return "LocalFile: " + getPath() + " id: " + getId();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setCreatedDateTime(long millis) {
        this.createdDateTime = millis;
    }

    @Override
    public long getCreatedDateTime() {
        return this.createdDateTime;
    }

    @Override
    public void setLastModifiedDateTime(long millis) {
        this.lastModifiedDateTime = millis;
    }

    @Override
    public long getLastModifiedDateTime() {
        return this.lastModifiedDateTime;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compare modification date of this file in regard to lastModifiedDateTime.
     * If {@link Item#getLastModifiedDateTime()} cannot be parsed
     * ModificationStatus.NOTMODIFIED is returned.
     * <p>
     * When metadata timestamps like lastModifiedDateTime are witten the
     * precision is dependant on the underlying filesystem. Linux/ext4fs looses
     * the milliseconds precision, therefor modification status is done on
     * second precision
     * </p>
     * 
     * @param item Item
     * @return ModificationStatus
     */
    @Override
    public ModificationStatus lastModificationStatus(Item item) {
        try {
            long itemLastModified = ISO8061
                .toCalendar(item.getLastModifiedDateTime()).getTimeInMillis();
            if ((itemLastModified - lastModifiedDateTime) > PRECISION_MS) {
                return ModificationStatus.OLDER;
            } else
                if ((lastModifiedDateTime - itemLastModified) > PRECISION_MS) {
                return ModificationStatus.NEWER;
            } else {
                return ModificationStatus.NOTMODIFIED;
            }
        } catch (ParseException e) {
            return ModificationStatus.NOTMODIFIED;
        }
    }

    @Override
    public void relateWith(Item item) {
        this.id = item.getId().toUpperCase();
        this.name = item.getName();
        this.createdDateTime = fromISO8601(item.getCreatedDateTime());
        this.lastModifiedDateTime = fromISO8601(item.getLastModifiedDateTime());
    }

    @Override
    public void updateItem(Item item) {
        item.setName(name);
        item.getFileSystemInfo()
            .setCreatedDateTime(ISO8061.fromMillis(createdDateTime));
        item.getFileSystemInfo()
            .setLastModifiedDateTime(ISO8061.fromMillis(lastModifiedDateTime));
    }

    /**
     * Convert ISO 8601 date to ms
     * 
     * @param timestamp String date in ISO 8601 format
     * @return long date in ms
     * @throws ParseException if timestamp fail to parse
     */
    long fromISO8601(String timestamp) {
        try {
            return ISO8061.toMS(timestamp);
        } catch (ParseException e) {
            throw new OneDriveException("Failure parsing timestamp: " + timestamp,
                                        e);
        }
    }

}
