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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import io.yucca.microsoft.onedrive.io.InputStreamingOutput;

/**
 * OneDriveContentMessageBodyWriter writes OneDriveContent
 * 
 * @author yucca.io
 */
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class OneDriveContentMessageBodyWriter
    implements MessageBodyWriter<OneDriveContent> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return OneDriveContent.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(OneDriveContent t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(OneDriveContent t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
                            throws IOException, WebApplicationException {
        try (InputStreamingOutput iso = new InputStreamingOutput(t
            .getInputStream())) {
            iso.write(entityStream);
        }
    }

}
