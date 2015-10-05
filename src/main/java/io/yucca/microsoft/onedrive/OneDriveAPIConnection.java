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
import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import io.yucca.microsoft.onedrive.util.JulFacade;

/**
 * Represents an authenticated connection to the OneDrive API
 * 
 * @author yucca.io
 */
public class OneDriveAPIConnection {

    private static final Logger LOG = LoggerFactory
        .getLogger(OneDriveAPIConnection.class);

    public static final String ONEDRIVE_URL = "https://api.onedrive.com/v1.0";

    public static final String ONEDRIVE_BUSINESS_URL = "https://{tenant}-my.sharepoint.com/_api/v2.0";

    private final OneDriveConfiguration configuration;

    private Client client;

    private ObjectMapper mapper;

    private OneDriveSession session;

    /**
     * Constructs the connection to the OneDrive API, the authorization is
     * delayed until the first API request
     * 
     * @param configuration OneDriveConfiguration
     * @throws ConfigurationException if configuration is invalid
     * @throws FileNotFoundException if configuration file does not exist
     */
    public OneDriveAPIConnection(OneDriveConfiguration configuration)
        throws FileNotFoundException, ConfigurationException {
        this.configuration = configuration;
        initialiseClient();
    }

    /**
     * Initialize Jersey Client
     */
    private void initialiseClient() {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        this.mapper = ClientFactory.createMapper(configuration,
                                                 jacksonProvider);
        this.client = ClientFactory.create(configuration, jacksonProvider);
        this.session = new OneDriveSession(configuration, client);
        if (configuration.isDebugLogging()) {
            this.client.register(new LoggingFilter(new JulFacade(LOG), true));
        }
    }

    /**
     * Get an access token.
     * 
     * @return TokenResult
     */
    TokenResult getAccessToken() {
        session.requestAccessToken();
        return session.getAccessToken();
    }

    /**
     * Get authorized client.
     * 
     * @return Client
     */
    public Client getClient() {
        return session.getClient();
    }

    /**
     * Get WebTarget based on authorized client.
     * <p>
     * TODO determine URL based on configuration parameter
     * </p>
     * 
     * @return WebTarget
     */
    public WebTarget webTarget() {
        return session.getClient().target(ONEDRIVE_URL);
    }

    /**
     * Get WebTarget based on authorized client.
     * 
     * @param uri URI to target
     * @return WebTarget
     */
    public WebTarget webTarget(URI uri) {
        return session.getClient().target(uri);
    }

    /**
     * Closes the client and all webTargets
     */
    public void close() {
        client.close();
    }

    /**
     * Logout from OneDrive API
     */
    public void logOut() {
        session.logOut();
    }

    /**
     * Get ObjectMapper
     * 
     * @return ObjectMapper
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Maps a Map<String, Object> to JSON
     * 
     * @param map Map<String, Object>
     * @return String json
     */
    public String mapToJson(Map<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new OneDriveException("Failure mapping to JSON", e);
        }
    }

}
