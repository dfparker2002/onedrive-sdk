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
package io.yucca.microsoft.onedrive;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.ws.rs.core.Response;

/**
 * OneDriveFile represents a file that can be uploaded or downloaded from
 * OneDrive. This explicitly extends {@link Closeable} to prevent auto closing
 * of the @{link {@link InputStream} by {@link Response#readEntity(Class)}
 *
 * @author yucca.io
 */
public class OneDriveFile implements OneDriveContent {

    private final Path file;

    private final String name;

    /**
     * Construct an OneDriveFile based on filename
     * 
     * @param name String filename
     * @throws FileNotFoundException if the file does not exist
     */
    public OneDriveFile(String name) throws FileNotFoundException {
        this.file = Paths.get(name);
        this.name = file.getFileName().toString();
        checkFileExistance(name);
    }

    /**
     * Construct an OneDriveFile based on path
     * 
     * @param file Path path to the file
     * @throws FileNotFoundException if the file does not exist
     */
    public OneDriveFile(Path file) throws FileNotFoundException {
        this.file = file;
        this.name = file.getFileName().toString();
        checkFileExistance(name);
    }

    /**
     * Construct an OneDriveFile based on path and filename
     * 
     * @param file Path path to the file
     * @param name String filename used in OneDrive
     * @throws FileNotFoundException if the file does not exist
     */
    public OneDriveFile(Path file, String name) throws FileNotFoundException {
        this.file = file;
        this.name = name;
        checkFileExistance(name);
    }

    /**
     * Get the path to the file
     * 
     * @return Path
     */
    public Path getFile() {
        return file;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (file == null) {
            throw new OneDriveException("File cannot be null");
        }
        try {
            return Files
                .newInputStream(file,
                                new OpenOption[] { StandardOpenOption.READ });
        } catch (FileNotFoundException e) {
            throw new OneDriveException("Failure getting inputstream", e);
        }
    }

    /**
     * Test existance of file
     * 
     * @param filename String
     * @throws FileNotFoundException
     */
    private void checkFileExistance(String filename)
        throws FileNotFoundException {
        if (!exists()) {
            throw new FileNotFoundException("File does not exist: " + filename);
        }
    }

    /**
     * Determine if file exists
     * 
     * @return boolean true if exists
     */
    public boolean exists() {
        return Files.exists(file, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public long getLength() throws IOException {
        return Files.size(file);
    }

    @Override
    public boolean isLarger(long length) throws IOException {
        return getLength() > length;
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }

}
