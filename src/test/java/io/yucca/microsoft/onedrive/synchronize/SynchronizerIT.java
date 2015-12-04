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
import io.yucca.microsoft.onedrive.OneDriveAPIConnectionImpl;
import io.yucca.microsoft.onedrive.OneDriveConfiguration;
import io.yucca.microsoft.onedrive.OneDriveImpl;
import io.yucca.microsoft.onedrive.addressing.ItemAddress;
import io.yucca.microsoft.onedrive.addressing.SpecialAddress;
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

    private LocalDriveRepository repository;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnectionImpl(configuration);
        this.tmpFolderPath = Paths.get(testFolder.getRoot().getAbsolutePath());

    }

    @Test
    public void testSynchronizeDrive() throws IOException {
        OneDrive remoteDrive = OneDriveImpl.defaultDrive(api);
        repository = new FileSystemRepository(tmpFolderPath,
                                              remoteDrive.getDrive());
        synchronizer = new Synchronizer(new FileSystemSynchronizer(repository),
                                        remoteDrive, api, configuration);
        synchronizer.synchronize(false);
        synchronizer.synchronize(true);
    }

    //@Test
    public void testSynchronizeFolder() throws IOException {
        OneDrive remoteDrive = OneDriveImpl.defaultDrive(api);
        repository = new FileSystemRepository(tmpFolderPath,
                                              remoteDrive.getDrive());
        ItemAddress folderAddress = new SpecialAddress(SpecialFolder.DOCUMENTS);
        Path localFolder = tmpFolderPath.resolve(ROOT_DOCUMENTS);
        synchronizer = new Synchronizer(new FileSystemSynchronizer(repository),
                                        remoteDrive, api, configuration);
        synchronizer.synchronize(localFolder, folderAddress, false);
        synchronizer.synchronize(true);
    }

}
