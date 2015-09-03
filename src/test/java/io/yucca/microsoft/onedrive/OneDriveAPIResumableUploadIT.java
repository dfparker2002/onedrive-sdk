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
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.yucca.microsoft.onedrive.resources.ConflictBehavior;
import io.yucca.microsoft.onedrive.resources.Item;
import io.yucca.microsoft.onedrive.resources.UploadSession;

public class OneDriveAPIResumableUploadIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private OneDriveAPIResumableUpload uploader;

    private UploadSession session;

    private String apiTestFolderId;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);
        this.uploader = new OneDriveAPIResumableUpload(api);
        this.apiTestFolderId = TestMother.createAPITestFolder(api).getId();
    }

    @Test
    public void testCreateAndCancelSession() throws FileNotFoundException {
        this.session = uploader
            .createSessionById(new OneDriveFile(TestMother.ITEM_UPLOAD_3_PATH),
                               apiTestFolderId, ConflictBehavior.REPLACE);
        assertNotNull(session);
        uploader.cancelSession(session);
    }

    @Test
    public void testUploadFragments()
        throws OneDriveResumableUploadException, IOException {
        OneDriveFile file = new OneDriveFile(TestMother.ITEM_UPLOAD_3_PATH);
        session = uploader.createSessionById(file, apiTestFolderId,
                                             ConflictBehavior.REPLACE);
        assertNotNull(session);
        Item item = uploader.uploadFragments(file, session);
        assertNotNull(item);
    }

    // exception handling should be mocked
    // conflictBehaviour null

    @After
    public void tearDown() {
        api.deleteById(apiTestFolderId);
        api.close();
    }

}
