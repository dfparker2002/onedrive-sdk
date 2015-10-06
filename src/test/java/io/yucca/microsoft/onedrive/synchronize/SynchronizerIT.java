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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.yucca.microsoft.onedrive.ConfigurationUtil;
import io.yucca.microsoft.onedrive.OneDrive;
import io.yucca.microsoft.onedrive.OneDriveAPIConnection;
import io.yucca.microsoft.onedrive.OneDriveConfiguration;
import io.yucca.microsoft.onedrive.OneDriveFolder;
import io.yucca.microsoft.onedrive.resources.SpecialFolder;

public class SynchronizerIT {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private static final String ROOT_DOCUMENTS = "root/APITest";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private Synchronizer synchronizer;

    private Path tmpFolderPath;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);
        this.tmpFolderPath = Paths.get(testFolder.getRoot().getAbsolutePath());
    }

    // @Test
    public void testSynchronizeDrive() throws IOException {
        OneDrive remoteDrive = OneDrive.defaultDrive(api);
        LocalDrive localDrive = new LocalDrive(tmpFolderPath,
                                               remoteDrive.getDrive());
        synchronizer = new Synchronizer(localDrive, remoteDrive, api,
                                        configuration);
        synchronizer.synchronize(false);
        synchronizer.synchronize(true);
    }

    /**
     * Folder hierarchy must already exist otherwise id's are not available or
     * the hierarchy should be created
     * 
     * @throws IOException
     * @throws ConfigurationException
     */
    // @Test
    public void testSynchronizeFolder() throws IOException {
        OneDrive remoteDrive = OneDrive.defaultDrive(api);
        OneDriveFolder remoteFolder = remoteDrive
            .getSpecialFolder(SpecialFolder.DOCUMENTS, null);
        LocalDrive localDrive = new LocalDrive(tmpFolderPath,
                                               remoteDrive.getDrive());
        LocalFolder localFolder = new LocalFolder(tmpFolderPath
            .resolve(ROOT_DOCUMENTS));
        synchronizer = new Synchronizer(localDrive, localFolder, remoteDrive,
                                        remoteFolder, api, configuration);
        synchronizer.synchronize(false);
        synchronizer.synchronize(true);
    }

}
