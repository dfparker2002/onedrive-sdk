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

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reading and writing of the configuration properties.
 *
 * @author yucca.io
 */
public final class ConfigurationUtil {

    private static final Logger LOG = LoggerFactory
        .getLogger(ConfigurationUtil.class);

    public static final String CLIENT_ID = "clientId";

    public static final String CLIENT_SECRET = "clientSecret";

    public static final String AUTHORIZATION_CODE = "authorizationCode";

    public static final String REFRESH_TOKEN = "refreshToken";

    public static final String DELTA_TOKEN = "deltaToken";

    public static final String READ_TIMEOUT = "readTimeout";

    public static final String CONNECTION_TIMEOUT = "connectionTimeout";

    public static final String DEBUG_LOGGING = "debugLogging";

    private static PropertiesConfiguration config = new PropertiesConfiguration();

    private ConfigurationUtil() {
    }

    /**
     * Read configuration from properties file
     * 
     * @param configurationFile String properties filename
     * @return OneDriveConfiguration
     * @throws FileNotFoundException
     * @throws ConfigurationException
     */
    public static OneDriveConfiguration read(String configurationFile)
        throws FileNotFoundException, ConfigurationException {
        LOG.info("Reading configuration from file: {}", configurationFile);
        File file = new File(configurationFile);
        if (!file.exists()) {
            throw new FileNotFoundException("Failure reading configuration, file: "
                                            + configurationFile
                                            + " does not exist");
        }
        config.setBasePath(file.getParent());
        config.load(file.getName());
        OneDriveConfiguration odc = new OneDriveConfiguration(configurationFile);
        odc.setClientId(config.getString(CLIENT_ID));
        odc.setClientSecret(config.getString(CLIENT_SECRET));
        odc.setAuthorizationCode(config.getString(AUTHORIZATION_CODE));
        odc.setRefreshToken(config.getString(REFRESH_TOKEN));
        odc.setDeltaToken(config.getString(DELTA_TOKEN));
        odc.setReadTimeout(config
            .getInt(READ_TIMEOUT, OneDriveConfiguration.READ_TIMEOUT_DEFAULT));
        odc.setConnectionTimeout(config
            .getInt(CONNECTION_TIMEOUT,
                    OneDriveConfiguration.CONNECTION_TIMEOUT_DEFAULT));
        odc.setDebugLogging(config
            .getBoolean(DEBUG_LOGGING,
                        OneDriveConfiguration.DEBUG_LOGGING_DEFAULT));
        return odc;
    }

    private static void setProperties(OneDriveConfiguration configuration) {
        config.clear();
        config.getLayout()
            .setComment(CLIENT_ID,
                        "The Client ID created for your application.");
        config.setProperty(CLIENT_ID, configuration.getClientId());
        config.getLayout()
            .setComment(CLIENT_SECRET,
                        "The Client secret (v1) created for your application.");
        config.setProperty(CLIENT_SECRET, configuration.getClientSecret());
        config.getLayout()
            .setComment(AUTHORIZATION_CODE,
                        "The authorization code you received in the first authentication request.");
        config.setProperty(AUTHORIZATION_CODE,
                           configuration.getAuthorizationCode());
        config.getLayout()
            .setComment(REFRESH_TOKEN,
                        "The refreshToken used for acquiring a new accessToken, initially this is empty and set after first token was acquired.");
        config.setProperty(REFRESH_TOKEN, configuration.getRefreshToken());
        config.getLayout()
            .setComment(DELTA_TOKEN,
                        "The deltaToken used for delta synchronization, initially this is empty and set after a full synchronization has taken place.");
        config.setProperty(DELTA_TOKEN, configuration.getDeltaToken());
        config.getLayout()
            .setComment(READ_TIMEOUT,
                        "The timeout in ms waiting to read data.");
        config.setProperty(READ_TIMEOUT, configuration.getReadTimeout());
        config.getLayout()
            .setComment(CONNECTION_TIMEOUT,
                        "The timeout in ms waiting in making the initial connection.");
        config.setProperty(CONNECTION_TIMEOUT,
                           configuration.getConnectionTimeout());
        config.getLayout()
            .setComment(DEBUG_LOGGING,
                        "Enables/disable debug logging of HTTP requests and responses.");
        config.setProperty(DEBUG_LOGGING, configuration.isDebugLogging());
    }

    /**
     * Save configuration
     * 
     * @param configuration OneDriveConfiguration
     * @throws ConfigurationException thrown if properties file cannot be saved
     */
    public static void save(OneDriveConfiguration configuration)
        throws ConfigurationException {
        LOG.info("Saved configuration to file: {}",
                 configuration.getConfigurationFile());
        setProperties(configuration);
        // XXX is this correct?
        config.setBasePath(".");
        config.save(configuration.getConfigurationFile());
    }

    /**
     * Save configuration to file
     * 
     * @param configuration OneDriveConfiguration
     * @param configFile String properties filename
     * @throws ConfigurationException thrown if properties file cannot be saved
     */
    public static void save(OneDriveConfiguration configuration,
                            String configFile) throws ConfigurationException {
        setProperties(configuration);
        config.save(new File(configFile));
    }

}
