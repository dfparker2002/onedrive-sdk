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

public class OneDriveItemIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private OneDrive drive;

    private OneDriveFile file;

    private OneDriveFolder apitestFolder;

    private OneDriveFolder movedFolder;

    private OneDriveItem uploaded;

    @Before
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);

        TestMother.deleteAPITestFolder(api);
        this.drive = OneDrive.defaultDrive(api);
        this.apitestFolder = drive.createFolder(TestMother.FOLDER_APITEST);
        this.file = new OneDriveFile(Paths.get(TestMother.ITEM_UPLOAD_1_PATH),
                                     TestMother.ITEM_UPLOAD_1);
        this.uploaded = apitestFolder.upload(file);
    }

    @Test
    public void testCopy() {
        OneDriveItem copyItem = uploaded.copy(apitestFolder,
                                              TestMother.ITEM_UPLOAD_1_COPY);
        assertNotNull(copyItem);
    }

    @Test
    public void testDownload() throws FileNotFoundException {
        assertNotNull(uploaded.download());
    }

    // disabled see javadoc @Test(expected = NotModifiedException.class)
    public void testDownloadNotModified() throws FileNotFoundException {
        assertNotNull(uploaded.download());
    }

    @Test
    public void testGetItem() throws FileNotFoundException {
        assertNotNull(uploaded.getItem());
    }

    @Test
    public void testMove() throws FileNotFoundException {
        movedFolder = apitestFolder.createFolder(TestMother.FOLDER_MOVED);
        assertNotNull(uploaded.move(movedFolder));
    }

    @Test
    public void testRename() throws FileNotFoundException {
        uploaded.rename(TestMother.ITEM_UPLOAD_1_COPY);
    }

    @After
    public void tearDown() {
        apitestFolder.delete();
        api.close();
    }
}
