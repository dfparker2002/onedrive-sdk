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

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

public class OneDriveItemIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private OneDrive drive;

    private OneDriveFile file;

    private OneDriveFolder apitest;

    @Before
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);

        // create test directory and file
        TestMother.createAPITestFolder(api);
        TestMother.uploadTestItem(api);

        this.file = new OneDriveFile(Paths.get(TestMother.ITEM_UPLOAD_1_PATH),
                                     TestMother.ITEM_UPLOAD_1);
        this.drive = OneDrive.defaultDrive(api);
        this.apitest = drive.getFolder(TestMother.FOLDER_APITEST);
    }

    @Test
    public void testCopy() {
        OneDriveItem uploaded = apitest.upload(file);
        OneDriveItem copyItem = uploaded.copy(apitest,
                                              TestMother.ITEM_UPLOAD_1_COPY);
        assertNotNull(copyItem);
        copyItem.delete();
        uploaded.delete();
    }

    @Test
    public void testDownload() throws FileNotFoundException {
        apitest.upload(file);
        OneDriveItem uploaded = drive.getItem(TestMother.FOLDER_APITEST + "/"
                                              + TestMother.ITEM_UPLOAD_1);
        assertNotNull(uploaded);
        OneDriveContent odf = uploaded.download();
        assertNotNull(odf);
        uploaded.delete();
    }

    // disabled see javadoc @Test(expected = NotModifiedException.class)
    public void testDownloadNotModified() throws FileNotFoundException {
        OneDriveItem uploaded = apitest.upload(file);
        assertNotNull(uploaded);
        OneDriveContent odf = uploaded.download();
        assertNotNull(odf);
        uploaded.delete();
    }

    @Test
    public void testGetItem() throws FileNotFoundException {
        OneDriveItem uploaded = apitest.upload(file);
        assertNotNull(uploaded);
        Item item = uploaded.getItem();
        assertNotNull(item);
        uploaded.delete();
    }

    @Test
    public void testMove() throws FileNotFoundException {
        OneDriveItem uploaded = apitest.upload(file);
        assertNotNull(uploaded);
        OneDriveItem moved = uploaded
            .move(drive.getSpecialFolder(SpecialFolder.DOCUMENTS, null));
        assertNotNull(moved);
        moved.delete();
    }

    @Test
    public void testRename() throws FileNotFoundException {
        OneDriveItem uploaded = apitest.upload(file);
        assertNotNull(uploaded);
        uploaded.rename(TestMother.ITEM_UPLOAD_1_COPY);
        uploaded.delete();
    }

    @After
    public void tearDown() {
        apitest.delete();
        api.close();
    }
}
