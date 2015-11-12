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
import java.nio.file.Path;
import java.text.ParseException;

import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveFile;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.util.ChecksumUtil;

/**
 * LocalFile acts as a local replica of a file stored in OneDrive.
 *
 * @author yucca.io
 */
public class LocalFileImpl extends LocalItemImpl implements LocalFile {

    private static final long serialVersionUID = 1584688343230415773L;

    private transient OneDriveContent content;

    /**
     * Construct a LocalFile stored in the LocalDrive. If the file exists the
     * OneDrive metadata attributes are read.
     * 
     * @param path Path to file
     * @param repository LocalDriveRepository
     * @throws IOException if reading metadata fails
     */
    public LocalFileImpl(Path path, LocalDriveRepository repository)
        throws IOException {
        this.name = path.getFileName().toString();
        this.path = path;
        this.repository = repository;
        if (repository.exists(this)) {
            repository.readMetadata(this);
        }
    }

    /**
     * Construct a LocalFile stored in the LocalDrive. If the file exists the
     * OneDrive metadata attributes are read.
     * 
     * @param path Path to file
     * @param content Content file contents
     * @param repository LocalDriveRepository
     * @throws IOException if reading metadata fails
     */
    public LocalFileImpl(Path path, OneDriveContent content,
                         LocalDriveRepository repository) throws IOException {
        this.name = path.getFileName().toString();
        this.path = path;
        this.content = content;
        this.repository = repository;
        if (repository.exists(this)) {
            repository.readMetadata(this);
        }
    }

    /**
     * Construct a LocalFile based on Item, to be created in the LocalDrive. The
     * OneDrive item provides the metadata.
     * 
     * @param item Item
     * @param parent LocalResource
     * @param repository LocalDriveRepository
     * @throws IOException if resolving parent fails
     * @throws ParseException if parsing of a timestamp fails
     */
    protected LocalFileImpl(Item item, LocalResource parent,
                            LocalDriveRepository repository) {
        this.path = parent.getPath().resolve(item.getName());
        this.repository = repository;
        relateWith(item);
    }

    @Override
    public OneDriveFile getContent() throws FileNotFoundException {
        return new OneDriveFile(path);
    }

    @Override
    public void update(Item item) throws IOException {
        relateWith(item);
        if (content != null) {
            repository.update(this, content);
        }
    }

    @Override
    public boolean isContentModified(Item item) throws IOException {
        String sha1 = item.getFile().getHashes().getSha1Hash();
        return !sha1.equalsIgnoreCase(ChecksumUtil.sha1(path));
    }

}
