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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * OneDriveContentMessageBodyReader reads an OneDriveContent
 * 
 * @author yucca.io
 */
public class OneDriveContentMessageBodyReader
    implements MessageBodyReader<OneDriveContent> {

    public static final Pattern FILENAME_PATTERN = Pattern
        .compile(".*filename=\"(.*)\"", Pattern.UNICODE_CHARACTER_CLASS);

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return OneDriveContent.class.isAssignableFrom(type);
    }

    @Override
    public OneDriveContent readFrom(Class<OneDriveContent> type,
                                    Type genericType, Annotation[] annotations,
                                    MediaType mediaType,
                                    MultivaluedMap<String, String> httpHeaders,
                                    InputStream entityStream)
                                        throws IOException {
        return new OneDriveInputStream(entityStream, getFilename(httpHeaders),
                                       getLength(httpHeaders));
    }

    /**
     * Determine length from Content-Length header
     * 
     * @param httpHeaders MultivaluedMap<String, String>
     * @return long length or 1 if Content-Length header is unavailable
     */
    long getLength(MultivaluedMap<String, String> httpHeaders) {
        String length = httpHeaders.getFirst("Content-Length");
        if (length == null || length.isEmpty()) {
            length = "1";
        }
        return Long.valueOf(length);
    }

    /**
     * Extract filename out of Content-Disposition header value, if not
     * available then left empty
     * 
     * @param httpHeaders MultivaluedMap<String, String>
     * @return String filename
     */
    String getFilename(MultivaluedMap<String, String> httpHeaders) {
        String contentDisposition = httpHeaders.getFirst("Content-Disposition");
        if (contentDisposition != null && !contentDisposition.isEmpty()) {
            Matcher m = FILENAME_PATTERN.matcher(contentDisposition);
            if (m.matches()) {
                return m.group(1);
            }
        }
        return "";
    }
}
