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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Test;

public class ConfigurationUtilTest {
    
    private static final String TEST_ONEDRIVE_PROPERTIES = "src/test/resources/onedrive-config-test.properties";

    private static final String TEST_ONEDRIVE_PROPERTIES_SAVED = "onedrive-config-test-saved.properties";

    @Test
    public void testRead()
        throws FileNotFoundException, ConfigurationException {
        OneDriveConfiguration config = ConfigurationUtil
            .read(TEST_ONEDRIVE_PROPERTIES);
        assertNotNull(config);
        assertEquals("000000010101", config.getClientId());
        assertEquals("randompassphrase", config.getClientSecret());
        assertEquals("xxxxxx-xxxx-xxxx-xxx-deadbeaf",
                     config.getAuthorizationCode());
        assertEquals("", config.getRefreshToken());
        assertEquals("111111111111", config.getDeltaToken());
        assertEquals(2000, config.getReadTimeout());
        assertEquals(500, config.getConnectionTimeout());
        assertEquals(true, config.isDebugLogging());
    }

    @Test
    public void testSave()
        throws FileNotFoundException, ConfigurationException {
        OneDriveConfiguration config = ConfigurationUtil
            .read(TEST_ONEDRIVE_PROPERTIES);
        ConfigurationUtil.save(config);
        ConfigurationUtil.save(config);
    }

    @Test
    public void testSaveToFile()
        throws FileNotFoundException, ConfigurationException {
        OneDriveConfiguration config = ConfigurationUtil
            .read(TEST_ONEDRIVE_PROPERTIES);
        ConfigurationUtil.save(config, TEST_ONEDRIVE_PROPERTIES_SAVED);
        ConfigurationUtil.save(config, TEST_ONEDRIVE_PROPERTIES_SAVED);
    }

    @Test
    public void testReadNotExisting() {
        try {
            ConfigurationUtil.read("unknown.properties");
            fail("Expected FileNotFoundException");
        } catch (FileNotFoundException | ConfigurationException e) {
            // Expected
        }
    }

    @After
    public void tearDown() {
        File file = new File(TEST_ONEDRIVE_PROPERTIES_SAVED);
        if (file.exists()) {
            file.delete();
        }
    }
}
