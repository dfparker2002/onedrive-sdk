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

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.junit.Before;
import org.junit.Test;

public class OneDriveAPIConnectionIT {

    private static final String CONFIGURATIONFILE = "src/test/resources/onedrive-integrationtest.properties";

    private OneDriveAPIConnection api;

    private OneDriveConfiguration configuration;

    @Before()
    public void setUp() throws FileNotFoundException, ConfigurationException {
        this.configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
        this.api = new OneDriveAPIConnection(configuration);
    }

    @Test
    public void testGetToken() {
        TokenResult token = api.getAccessToken();
        assertNotNull(token);
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getExpiresIn());
        assertNotNull(token.getRefreshToken());
        assertNotNull(token.getTokenType());
    }

    @Test
    public void testLogout() {
        api.logOut();
    }

}
