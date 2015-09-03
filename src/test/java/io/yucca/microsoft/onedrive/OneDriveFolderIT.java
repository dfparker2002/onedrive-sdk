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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.resources.SpecialFolder;

public class OneDriveFolderIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private OneDrive drive;

    private OneDriveFolder apitest;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);

        // create test directory and file
        TestMother.createAPITestFolder(api);
        TestMother.uploadTestItem(api);

        this.drive = OneDrive.defaultDrive(api);
        this.apitest = drive.getFolder(TestMother.FOLDER_APITEST);
    }

    @Test
    public void testCreateAndDeleteFolder() {
        OneDrive drive = OneDrive.defaultDrive(api);
        OneDriveFolder createFolder = drive.getFolder(TestMother.FOLDER_APITEST)
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        createFolder.delete();
    }

    @Test
    public void testCopyFolder() {
        OneDrive drive = OneDrive.defaultDrive(api);
        OneDriveFolder apitest = drive.getFolder(TestMother.FOLDER_APITEST);
        OneDriveFolder copyFolder = apitest.copy(apitest,
                                                 TestMother.FOLDER_COPY);
        assertNotNull(copyFolder);
        copyFolder.delete();
    }

    @Test
    public void testListChildren() {
        OneDrive drive = OneDrive.defaultDrive(api);
        OneDriveFolder apitest = drive.getFolder(TestMother.FOLDER_APITEST);
        Collection<OneDriveItem> children = apitest
            .listChildren(TestMother.fullQueryParameters());
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    @Test
    public void testMoveToSpecialFolder() {
        OneDrive drive = OneDrive.defaultDrive(api);
        OneDriveFolder createFolder = drive.getFolder(TestMother.FOLDER_APITEST)
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        OneDriveFolder movedFolder = createFolder.move(SpecialFolder.DOCUMENTS);
        assertNotNull(movedFolder);
        movedFolder.delete();
    }

    @Test
    public void testMoveToDriveRoot() {
        OneDrive drive = OneDrive.defaultDrive(api);
        OneDriveFolder createFolder = drive.getFolder(TestMother.FOLDER_APITEST)
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        OneDriveFolder movedFolder = createFolder.move(drive);
        assertNotNull(movedFolder);
        movedFolder.delete();
    }

    @Test
    public void testRename() {
        OneDrive drive = OneDrive.defaultDrive(api);
        OneDriveFolder createFolder = drive.getFolder(TestMother.FOLDER_APITEST)
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        createFolder.rename(TestMother.FOLDER_RENAMED);
        createFolder.delete();
    }

    @Test
    public void testSearch() {
        OneDrive drive = OneDrive.defaultDrive(api);
        assertNotNull(drive);
        Collection<OneDriveItem> children = drive
            .getFolder(TestMother.FOLDER_APITEST)
            .search("e", TestMother.fullQueryParameters());
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_1_PATH), TestMother.ITEM_UPLOAD_1);
        OneDrive drive = OneDrive.defaultDrive(api);
        assertNotNull(drive);
        OneDriveItem uploaded = drive.getFolder(TestMother.FOLDER_APITEST)
            .upload(file);
        assertNotNull(uploaded);
        uploaded.delete();
    }

    @After
    public void tearDown() {
        apitest.delete();
        api.close();
    }
}
