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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.synchronize.FileSystemSynchronizer;
import io.yucca.microsoft.onedrive.synchronize.LocalDriveSynchronizer;
import io.yucca.microsoft.onedrive.synchronize.Synchronizer;

/**
 * Example in synchronizing an OneDrive to local filesystem and vise-versa
 * through the OneDrive Java SDK
 *
 * @author yucca.io
 */
public class OneDriveSynchronizerExample {

    private static final Logger LOG = LoggerFactory
        .getLogger(OneDriveSynchronizerExample.class);

    private static final String CONFIGURATIONFILE = "src/example/resources/onedrive-test.properties";

    private static OneDriveSynchronizerExample odse;

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    private Synchronizer synchronizer;

    public OneDriveSynchronizerExample() {
        try {
            this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
            this.api = new OneDriveAPIConnectionImpl(configuration);
        } catch (FileNotFoundException | ConfigurationException e) {
            LOG.error("Failed reading configuration", e);
        }
    }

    public static void main(String[] args) {
        odse = new OneDriveSynchronizerExample();
        odse.synchronize();
    }

    public void synchronize() {
        try {
            OneDrive onedrive = OneDriveImpl.defaultDrive(api);
            Path localPath = Paths
                .get(System.getProperty("user.home") + "/onedrive");
            LocalDriveSynchronizer fss = new FileSystemSynchronizer(localPath,
                                                                    onedrive);
            synchronizer = new Synchronizer(fss, api, configuration);
            synchronizer.synchronize(false);
        } catch (IOException | OneDriveException e) {
            LOG.error("Failure synchronizing OneDrive", e);
        }

    }

}
