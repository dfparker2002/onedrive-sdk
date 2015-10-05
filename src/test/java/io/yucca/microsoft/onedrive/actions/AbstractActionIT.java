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
package io.yucca.microsoft.onedrive.actions;

import java.io.FileNotFoundException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;

import io.yucca.microsoft.onedrive.ConfigurationUtil;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveConfiguration;
import io.yucca.microsoft.onedrive.TestMother;
import io.yucca.microsoft.onedrive.addressing.IdAddress;

public abstract class AbstractActionIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    protected OneDriveAPIConnection api;

    protected OneDriveConfiguration configuration;

    protected String uploadedItemId;

    protected String apiTestFolderId;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);
        this.apiTestFolderId = TestMother.createAPITestFolder(api).getId();
        this.uploadedItemId = TestMother.uploadTestItem(api).getId();
    }

    @After
    public void tearDown() {
        if (apiTestFolderId != null) {
            new DeleteAction(api, new IdAddress(apiTestFolderId)).call();
        }
        api.close();
    }
}
