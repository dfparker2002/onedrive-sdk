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

/**
 * Example in using the OneDrive Java SDK
 *
 * @author yucca.io
 */
public class OneDriveExample {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private static OneDriveAPIConnection api;

    private static OneDriveConfiguration configuration;

    public static void main(String[] args) {
        try {
            configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
            api = new OneDriveAPIConnection(configuration);
            info();
            list();
        } catch (FileNotFoundException | ConfigurationException e) {
            System.out.println("Failed reading OneDrive configuration. " + e);
        }
    }

    public static void info() {
        OneDrive drive = OneDrive.defaultDrive(api);
        System.out.println("Hello user: " + drive.getUser().getDisplayName());
    }

    public static void list() {
        OneDrive drive = OneDrive.defaultDrive(api);
        System.out.println("The OneDrive contains the following folders: ");
        for (OneDriveItem item : drive.listChildren()) {
            if (item instanceof OneDriveFolder) {
                System.out.format(" %s\n", item.getItem().getName());
            }
        }
    }
}
