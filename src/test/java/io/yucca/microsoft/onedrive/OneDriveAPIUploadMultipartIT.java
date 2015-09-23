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

import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;

public class OneDriveAPIUploadMultipartIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private String apiTestFolderId;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);
        this.apiTestFolderId = TestMother.createAPITestFolder(api).getId();
    }

    @Test
    public void testUploadMultipartByParentId()
        throws FileNotFoundException, OneDriveResumableUploadException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_3_PATH), TestMother.ITEM_UPLOAD_3);
        Item item = api.uploadMultipartByParentId(file, apiTestFolderId,
                                                  ConflictBehavior.FAIL);
        assertNotNull(item);
    }

    @Test
    public void testUploadMultipartByParentPath()
        throws FileNotFoundException, OneDriveResumableUploadException {
        OneDriveFile file = new OneDriveFile(Paths
            .get(TestMother.ITEM_UPLOAD_3_PATH), TestMother.ITEM_UPLOAD_3);
        Item item = api.uploadMultipartByParentPath(file,
                                                    TestMother.FOLDER_APITEST,
                                                    ConflictBehavior.FAIL);
        assertNotNull(item);
    }

    @After
    public void tearDown() {
        if (apiTestFolderId != null) {
            api.deleteById(apiTestFolderId);
        }
        api.close();
    }
}
