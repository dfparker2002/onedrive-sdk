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

import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

public class OneDriveIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnectionImpl api;

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
    public void testDefaultDrive() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        assertNotNull(drive.getDriveId());
    }

    @Test
    public void testGetDrive() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        assertNotNull(drive.getDrive());
    }

    @Test
    public void testGetDriveById() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        assertNotNull(OneDriveImpl.byDriveId(api, drive.getDriveId()));
    }

    @Test
    public void testGetFolder() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        OneDriveFolder folder = drive.getFolder(TestMother.FOLDER_APITEST);
        assertNotNull(folder);
    }

    @Test
    public void testGetItem() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_1_PATH), TestMother.ITEM_UPLOAD_1);
        OneDriveItem uploaded = drive.upload(file);
        assertNotNull(drive.getItem(TestMother.ITEM_UPLOAD_1));
        uploaded.delete();
    }

    @Test
    public void testCreateFolder() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        OneDriveFolder folder = drive.createFolder(TestMother.FOLDER_CREATE,
                                                   ConflictBehavior.RENAME);
        assertNotNull(folder);
        folder.delete();
    }

    @Test
    public void testGetSpecialFolder() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        OneDriveFolder folder = drive.getSpecialFolder(SpecialFolder.DOCUMENTS,
                                                       null);
        assertNotNull(folder);
    }

    @Test
    public void testGetRootFolder() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        OneDriveFolder folder = drive.getRootFolder();
        assertNotNull(folder);
    }

    @Test
    public void testListChildren() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        Collection<OneDriveItemImpl> children = drive
            .listChildren(TestMother.listChildrenQueryParameters());
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    @Test
    public void testSearch() {
        OneDrive drive = OneDriveImpl.defaultDrive(api);
        assertNotNull(drive);
        Collection<OneDriveItemImpl> children = drive
            .search("e", TestMother.searchQueryParameters());
        assertNotNull(children);
        assertTrue(children.size() > 0);
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_1_PATH), TestMother.ITEM_UPLOAD_1);
        OneDriveItem uploaded = drive.upload(file);
        assertNotNull(uploaded);
        uploaded.delete();
    }

    @After
    public void tearDown() {
        apitestFolder.delete();
        api.close();
    }
}
