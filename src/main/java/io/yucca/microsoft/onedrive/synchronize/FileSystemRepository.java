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
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveContent;
import io.yucca.microsoft.onedrive.OneDriveException;
import io.yucca.microsoft.onedrive.io.InputStreamingOutput;

/**
 * FilesystemRepository defines a repository for storing {@link LocalResource}
 * on a filesystem. Extended Attributes are used to create a relationship to an
 * OneDrive item via the Item identifier and saved metadata properties.
 * 
 * @author yucca.io
 */
public class FileSystemRepository implements LocalDriveRepository {

    private static final Logger LOG = LoggerFactory
        .getLogger(FileSystemRepository.class);

    public static final String ATTRIBUTE_ONEDRIVE_ITEMID = "onedrive.id";

    private final Path drivePath;

    private final OneDrive onedrive;

    private final LocalDriveImpl localDrive;

    /**
     * FilesystemRepository
     * 
     * @param drivePath Path base path to LocalDrive
     * @param onedrive OneDrive
     * @throws IOException if repository initialization fails
     */
    public FileSystemRepository(Path drivePath, OneDrive onedrive)
        throws IOException {
        this.drivePath = drivePath;
        this.onedrive = onedrive;
        this.localDrive = new LocalDriveImpl(onedrive.getDrive(), this);
        initialize(localDrive);
    }

    /**
     * FilesystemRepository
     * 
     * @param drivePath Path base path to LocalDrive
     * @param onedrive OneDrive
     * @throws IOException if repository initialization fails
     */
    public FileSystemRepository(Path drivePath, OneDrive onedrive,
                                String itemId) throws IOException {
        this.drivePath = drivePath;
        this.onedrive = onedrive;
        this.localDrive = new LocalDriveImpl(onedrive.getDrive(), this);
        initialize(localDrive);
    }

    /**
     * Initialize the store for the LocalDrive, determines if extended
     * attributes are supported and enabled for the local filesystem
     * 
     * @param drive LocalDrive
     * @throws IOException
     * @throws OneDriveException if extended attributes are not supported or
     *             enabled
     */
    private void initialize(LocalDrive drive) throws IOException {
        LOG.info("Initializing the root of LocalDrive: {}",
                 localDrive.getName());
        hasExtendedAttributesCapability(drive.getPath());
        createFolder(drive);
    }

    @Override
    public void createFolder(LocalFolder resource) throws IOException {
        if (!exists(resource)) {
            Files.createDirectories(resource.getPath());
        }
        writeMetadata(resource);
    }

    @Override
    public void createFile(LocalFile resource, OneDriveContent content)
        throws IOException {
        writeMetadataAndContent(resource, content);
    }

    @Override
    public void delete(LocalItem resource) throws IOException {
        Files.delete(resource.getPath());
    }

    @Override
    public boolean exists(LocalItem resource) {
        return Files.exists(resource.getPath(), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public LocalDriveImpl getLocalDrive() {
        return localDrive;
    }

    @Override
    public boolean isLocalDriveRoot(LocalResource folder) {
        return localDrive.getPath().equals(folder.getPath());
    }

    @Override
    public Path getPath() {
        return drivePath;
    }

    @Override
    public void rename(LocalItem resource, String name) throws IOException {
        if (!isRenamed(resource, name)) {
            return;
        }
        Path path = resource.getPath();
        Path renamed = Files.move(resource.getPath(),
                                  path.resolveSibling(resource.getName()),
                                  new CopyOption[] {});
        resource.setPath(renamed);
        resource.setName(name);
    }

    private boolean isRenamed(LocalItem resource, String name) {
        return !resource.getPath().getFileName().toString().equals(name);
    }

    @Override
    public void update(LocalItem resource, OneDriveContent content)
        throws IOException {
        rename(resource, resource.getName());
        writeMetadataAndContent(resource, content);
        if (!isLocalDriveRoot(resource) && !isLocalDriveRoot(resource.getParent())) {
            resetTimestamps(resource.getParent());
        }
    }

    private void writeMetadataAndContent(LocalItem resource,
                                         OneDriveContent content)
                                             throws IOException {
        if (content != null) {
            write(resource, content);
        }
        writeMetadata(resource);
    }

    private void write(LocalItem resource, OneDriveContent content)
        throws IOException {
        InputStreamingOutput iso = new InputStreamingOutput(content
            .getInputStream());
        OutputStream out = Files
            .newOutputStream(resource.getPath(), StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING);
        iso.write(out);
        out.close();
    }

    @Override
    public void resetTimestamps(LocalResource resource) throws IOException {
        if (ResourceType.FOLDER.equals(resource.type())) {
            Files.setLastModifiedTime(resource.getPath(), FileTime
                .fromMillis(resource.getLastModifiedDateTime()));
        }
    }

    @Override
    public void readMetadata(LocalItem resource) throws IOException {
        BasicFileAttributeView basicView = Files
            .getFileAttributeView(resource.getPath(),
                                  BasicFileAttributeView.class);
        BasicFileAttributes basicAttrs = basicView.readAttributes();
        resource.setId(MetadataUtil.readAttribute(resource.getPath(),
                                                  ATTRIBUTE_ONEDRIVE_ITEMID));
        resource.setPath(resource.getPath());
        resource.setName(resource.getPath().getFileName().toString());
        resource.setCreatedDateTime(basicAttrs.creationTime().toMillis());
        resource
            .setLastModifiedDateTime(basicAttrs.lastModifiedTime().toMillis());
    }

    @Override
    public void writeMetadata(LocalItem resource) throws IOException {
        Files.setLastModifiedTime(resource.getPath(), FileTime
            .fromMillis(resource.getLastModifiedDateTime()));
        MetadataUtil.writeAttribute(resource.getPath(),
                                    ATTRIBUTE_ONEDRIVE_ITEMID,
                                    resource.getId());
    }

    @Override
    public OneDrive getOneDrive() {
        return onedrive;
    }

    public Path getDrivePath() {
        return drivePath;
    }

    private void hasExtendedAttributesCapability(Path path) {
        try {
            MetadataUtil.readAttribute(path, ATTRIBUTE_ONEDRIVE_ITEMID);
        } catch (UnsupportedOperationException | IOException e) {
            throw new OneDriveException("The extended attributes capability is disabled for the filesystem or cannot be determined."
                                        + "This which is mandatory for the synchronization process to function, this depends on "
                                        + "extended attributes to relate the local item with the corresponding OneDrive item by  "
                                        + "storing the Item id as extended attribute. Enable this on Linux by remounting the partition "
                                        + "with user_xattr flag. For more information see https://docs.oracle.com/javase/tutorial/essential/io/fileAttr.html#user",
                                        e);
        }
    }

}
