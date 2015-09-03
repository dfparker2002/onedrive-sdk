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

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example in using the OneDrive Java SDK
 *
 * @author yucca.io
 */
public class OneDriveExample {

    private static final Logger LOG = LoggerFactory
        .getLogger(OneDriveExample.class);

    private static final String CONFIGURATIONFILE = "src/example/resources/onedrive-test.properties";

    private static OneDriveExample ode;

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    public static void main(String[] args) {
        ode = new OneDriveExample();
        ode.info();
        ode.list();
    }

    public OneDriveExample() {
        try {
            this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
            this.api = new OneDriveAPIConnection(configuration);
        } catch (FileNotFoundException | ConfigurationException e) {
            LOG.error("Failed reading configuration", e);
        }
    }

    public void info() {
        OneDrive drive = OneDrive.defaultDrive(api);
        LOG.info("Hello user: {}", drive.getUser().getDisplayName());
    }

    public void list() {
        OneDrive drive = OneDrive.defaultDrive(api);
        LOG.info("OneDrive contains the following folders:");
        for (OneDriveItem item : drive.listChildren()) {
            if (item instanceof OneDriveFolder) {
                LOG.info(item.getItem().getName());
            }
        }
    }
}
