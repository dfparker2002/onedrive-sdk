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
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

/**
 * OneDriveContent represents content that can be uploaded or downloaded from
 * OneDrive. This explicitly extends {@link Closeable} to prevent auto closing
 * of the @{link {@link InputStream} by {@link Response#readEntity(Class)}
 * 
 * @author yucca.io
 */
public interface OneDriveContent extends Closeable {

    /**
     * Get an InputStream to the content
     * 
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get the name of the content
     * 
     * @return String
     */
    String getName();

    /**
     * Get the length of the content
     * 
     * @return long
     * @throws IOException
     */
    long getLength() throws IOException;

    /**
     * See if content length is larger than
     * 
     * @param length long threshold
     * @return true if larger
     * @throws IOException
     */
    boolean isLarger(long length) throws IOException;
}
