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

    private OneDriveFolder apitestFolder;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnectionImpl(configuration);

        // create test directory and file
        TestMother.createAPITestFolder(api);
        TestMother.uploadTestItem(api);

        this.drive = OneDriveImpl.defaultDrive(api);
        this.apitestFolder = drive.getFolder(TestMother.FOLDER_APITEST);
    }

    @Test
    public void testCreateAndDeleteFolder() {
        OneDriveFolder createFolder = apitestFolder
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        createFolder.delete();
    }

    @Test
    public void testCopyFolder() {
        OneDriveFolder copyFolder = apitestFolder.copy(apitestFolder,
                                                       TestMother.FOLDER_COPY);
        assertNotNull(copyFolder);
    }

    @Test
    public void testGetFolder() {
        apitestFolder.createFolder(TestMother.FOLDER_CREATE);
        OneDriveFolder folder = apitestFolder
            .getFolder(TestMother.FOLDER_CREATE);
        assertNotNull(folder);
    }

    @Test
    public void testGetItem() throws FileNotFoundException {
        assertNotNull(apitestFolder.getItem(TestMother.ITEM_UPLOAD_1));
    }

    @Test
    public void testListChildren() {
        Collection<OneDriveItem> children = apitestFolder
            .listChildren(TestMother.fullQueryParameters());
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    @Test
    public void testMoveToSpecialFolder() {
        OneDriveFolder createFolder = apitestFolder
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        OneDriveFolder movedFolder = createFolder.move(SpecialFolder.DOCUMENTS);
        assertNotNull(movedFolder);
        movedFolder.delete();
    }

    @Test
    public void testMoveToDriveRoot() {
        OneDriveFolder createFolder = apitestFolder
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        OneDriveFolder movedFolder = createFolder.move(drive);
        assertNotNull(movedFolder);
        movedFolder.delete();
    }

    @Test
    public void testRename() {
        OneDriveFolder createFolder = apitestFolder
            .createFolder(TestMother.FOLDER_CREATE);
        assertNotNull(createFolder);
        createFolder.rename(TestMother.FOLDER_RENAMED);
        createFolder.delete();
    }

    @Test
    public void testSearch() {
        Collection<OneDriveItem> children = apitestFolder
            .search("e", TestMother.fullQueryParameters());
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_2_PATH), TestMother.ITEM_UPLOAD_2);
        OneDriveItem uploaded = apitestFolder.upload(file);
        assertNotNull(uploaded);
    }

    @After
    public void tearDown() {
        apitestFolder.delete();
        api.close();
    }
}
