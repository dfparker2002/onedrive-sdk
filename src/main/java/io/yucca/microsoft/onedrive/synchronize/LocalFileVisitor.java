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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileVisitor implementation used to traverse the LocalDrive thereby
 * registering items for synchronization.
 *
 * @author yucca.io
 */
public class LocalFileVisitor implements FileVisitor<Path> {

    private static final Logger LOG = LoggerFactory
        .getLogger(LocalFileVisitor.class);

    private final LocalDriveSynchronizer synchronizer;

    /**
     * Constructor
     * 
     * @param synchronizer LocalDriveSynchronizer
     */
    public LocalFileVisitor(LocalDriveSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    /**
     * If a directory is visited and no onedrive.id metadata attribute is set,
     * then push this directory onto the additions set for creation in OneDrive,
     * otherwise push in onto the folders set for reference by files/item.
     * <p>
     * The additions set is sorted, so in processing these additions, the
     * OneDrive folder is created before any files are uploaded after which it
     * is added to the folder set for reference. Caveat: this will only work if
     * the additions are sequential processed or in parallel if on uploading
     * files a check is made that a onedrive.id attribute exists in the parent
     * directory or the folder exists in OneDrive.
     * </p>
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs)
                                                 throws IOException {
        LocalFolder folder = new LocalFolderImpl(dir, synchronizer);
        if (synchronizer.isLocalDriveRoot(folder)) {
            synchronizer.registerFolder(folder);
            return FileVisitResult.CONTINUE;
        }
        if (folder.hasId()) {
            synchronizer.registerFolder(folder);
        } else {
            synchronizer.registerAddition(folder);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
        LocalFile localFile = new LocalFileImpl(file, synchronizer);
        if (localFile.hasId()) {
            synchronizer.registerItem(localFile);
        } else {
            synchronizer.registerAddition(localFile);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
        throws IOException {
        LOG.warn("Failure visiting file: {} for processing, skipping.",
                 file.toUri(), exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        throws IOException {
        LocalFolder folder = new LocalFolderImpl(dir, synchronizer);
        if (synchronizer.isLocalDriveRoot(folder)) {
            return FileVisitResult.CONTINUE;
        }
        if (folder.hasId()) {
            synchronizer.registerItem(folder);
        }
        return FileVisitResult.CONTINUE;
    }

}
