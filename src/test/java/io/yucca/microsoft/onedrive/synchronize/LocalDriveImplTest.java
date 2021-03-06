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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LocalDriveImplTest {

    private LocalDriveRepository repository;

    private LocalDrive localDrive;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        Path folder = Paths.get(testFolder.getRoot().getAbsolutePath());
        repository = new FileSystemRepository(folder, new OneDriveStub());
        localDrive = repository.getLocalDrive();
    }

    @Test
    public void testExists() {
        assertTrue(localDrive.exists());
    }

    @Test
    public void testGetId() {
        assertEquals("1!0", localDrive.getId());
    }

    @Test
    public void testGetName() {
        assertEquals(LocalDriveImpl.LOCALDRIVE, localDrive.getName());
    }

    @Test
    public void testGetPath() {
        assertEquals(Paths.get(testFolder.getRoot().getAbsolutePath()),
                     localDrive.getPath());
    }

    @Test
    public void testHasId() {
        assertTrue(localDrive.hasId());
    }

}
