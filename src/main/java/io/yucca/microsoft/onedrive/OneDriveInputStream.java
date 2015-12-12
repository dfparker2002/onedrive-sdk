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

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

/**
 * OneDriveInputStream, content represented by an InputStream. When the
 * OneDriveInputStream is acquired via a call to
 * {@link Response#readEntity(Class)} or by using
 * {@link OneDriveContentMessageBodyReader}, it is important to call
 * {@link OneDriveInputStream#close()} manually when reading the stream has
 * finished, this will close underlying connection.
 *
 * @author yucca.io
 */
public class OneDriveInputStream implements OneDriveContent {

    private final InputStream stream;

    private final String name;

    private final long length;

    /**
     * Constructor
     * 
     * @param stream InputStream represent the stream to read the contents
     * @param name String name of the content
     */
    public OneDriveInputStream(InputStream stream, String name) {
        this.stream = stream;
        this.name = name;
        this.length = 0;
    }

    /**
     * Constructor
     * 
     * @param stream InputStream represent the stream to read the contents
     * @param name String name of the content
     * @param length long length of the content
     */
    public OneDriveInputStream(InputStream stream, String name, long length) {
        this.stream = stream;
        this.name = name;
        this.length = length;
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public boolean isLarger(long length) {
        return this.length > length;
    }

    /**
     * Close the InputStream
     * 
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

}
