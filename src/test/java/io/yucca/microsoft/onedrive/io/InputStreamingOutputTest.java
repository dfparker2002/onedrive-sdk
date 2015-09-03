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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.TestMother;

public class InputStreamingOutputTest {

    private File file;

    @Before
    public void setUp() throws FileNotFoundException {
        file = new File(TestMother.ITEM_UPLOAD_1_PATH);
    }

    @Test
    public void testWriteTotal() throws IOException {
        try (
            InputStreamingOutput iso = new InputStreamingOutput(new FileInputStream(file))) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            iso.write(b);
            assertEquals(file.length(), b.size());
        }
    }

}
