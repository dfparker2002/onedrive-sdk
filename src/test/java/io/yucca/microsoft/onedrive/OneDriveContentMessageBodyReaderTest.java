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

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

public class OneDriveContentMessageBodyReaderTest {

    private OneDriveContentMessageBodyReader reader;

    private MultivaluedMap<String, String> httpHeaders;

    @Before
    public void setUp() {
        this.httpHeaders = new MultivaluedHashMap<>();
        this.reader = new OneDriveContentMessageBodyReader();
    }

    @Test
    public void testGetFilename() {
        httpHeaders.add("Content-Disposition",
                        "attachment; filename=\"Document.docx\"");
        assertEquals("Document.docx", reader.getFilename(httpHeaders));
    }

    @Test
    public void testGetFilenameUnknown() {
        assertEquals("", reader.getFilename(httpHeaders));
    }

    @Test
    public void testGetLength() {
        httpHeaders.add("Content-Length", "126");
        assertEquals(126, reader.getLength(httpHeaders));
    }

    @Test
    public void testGetLengthUnknown() {
        assertEquals(1, reader.getLength(httpHeaders));
    }

}
