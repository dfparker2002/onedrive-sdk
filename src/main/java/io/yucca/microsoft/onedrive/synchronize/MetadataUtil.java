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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * MetadataUtil
 * 
 * @author yucca.io
 */
public final class MetadataUtil {

    private MetadataUtil() {
    }

    /**
     * Read a user defined metadata attribute
     * 
     * @param path Path to read from
     * @param name String attribute name
     * @return String attribute value, {@code null} if attribute does not exist
     * @throws IOException if attribute cannot be read
     */
    public static String readAttribute(Path path, String name)
        throws IOException {
        UserDefinedFileAttributeView userView = Files
            .getFileAttributeView(path, UserDefinedFileAttributeView.class);
        if (userView.list().contains(name)) {
            ByteBuffer buf = ByteBuffer.allocate(userView.size(name));
            userView.read(name, buf);
            buf.flip();
            return Charset.defaultCharset().decode(buf).toString();
        } else {
            return null;
        }
    }

    /**
     * Write a user defined metadata attribute
     * 
     * @param path Path to write to
     * @param name String attribute name
     * @param value String attribute value
     * @throws IOException if attribute cannot be written
     */
    public static void writeAttribute(Path path, String name, String value)
        throws IOException {
        UserDefinedFileAttributeView userView = Files
            .getFileAttributeView(path, UserDefinedFileAttributeView.class);
        userView.write(name, Charset.defaultCharset().encode(value));
    }

}
