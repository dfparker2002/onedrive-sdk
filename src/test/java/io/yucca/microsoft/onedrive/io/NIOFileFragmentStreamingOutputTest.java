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
package io.yucca.microsoft.onedrive.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.TestMother;

public class NIOFileFragmentStreamingOutputTest {

    private NIOFileFragmentStreamingOutput ffso;

    private Path file;

    @Before
    public void setUp() throws FileNotFoundException {
        file = Paths.get(TestMother.ITEM_UPLOAD_1_PATH);
    }

    @Test
    public void testWriteTotal() throws IOException {
        Range range = new Range(0, Files.size(file), Files.size(file));
        ffso = new NIOFileFragmentStreamingOutput(file, range);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ffso.write(b);
        assertEquals(Files.size(file), b.size());
    }

    @Test
    public void testWriteRange() throws IOException {
        Range range = new Range(0, 10, Files.size(file));
        ffso = new NIOFileFragmentStreamingOutput(file, range);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ffso.write(b);
        assertEquals(11, b.size());
        // test that outputstream is still open
        b.write(1);
    }

}
