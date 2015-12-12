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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import io.yucca.microsoft.onedrive.util.JulFacade;

/**
 * Represents an authenticated connection to the OneDrive API
 * 
 * @author yucca.io
 */
public class OneDriveAPIConnectionImpl
    implements AutoCloseable, OneDriveAPIConnection {

    private static final Logger LOG = LoggerFactory
        .getLogger(OneDriveAPIConnectionImpl.class);

    public static final String ONEDRIVE_URL = "https://api.onedrive.com/v1.0";

    public static final String ONEDRIVE_BUSINESS_URL = "https://{tenant}-my.sharepoint.com/_api/v2.0";

    private final OneDriveConfiguration configuration;

    private Client client;

    private ObjectMapper mapper;

    private OneDriveSession session;

    /**
     * Constructs the connection to the OneDrive API, the authorization is
     * delayed until the first API request.
     * 
     * @param configuration OneDriveConfiguration
     * @throws ConfigurationException if configuration is invalid
     * @throws FileNotFoundException if configuration file does not exist
     */
    public OneDriveAPIConnectionImpl(OneDriveConfiguration configuration)
        throws FileNotFoundException, ConfigurationException {
        LOG.info("Creating connection to OneDrive: {}", ONEDRIVE_URL);
        this.configuration = configuration;
        initialiseClient();
        LOG.info("Successfully established connection to OneDrive");
    }

    /**
     * Initialize Jersey Client.
     */
    private void initialiseClient() {
        LOG.info("Initializing Jersey client");
        JacksonJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        this.mapper = ClientFactory.createMapper(jacksonProvider);
        this.client = ClientFactory.create(configuration, jacksonProvider);
        this.session = new OneDriveSession(configuration, client);
        if (configuration.isDebugLogging()) {
            this.client.register(new LoggingFilter(new JulFacade(LOG), true));
        }
    }

    @Override
    public Client getClient() {
        return session.getClient();
    }

    @Override
    public boolean isAuthorized() {
        return session.hasAccessToken();
    }

    @Override
    public WebTarget webTarget() {
        return session.getClient().target(ONEDRIVE_URL);
    }

    @Override
    public WebTarget webTarget(URI uri) {
        return session.getClient().target(uri);
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void logOut() {
        session.logOut();
    }

    @Override
    public ObjectMapper getMapper() {
        return mapper;
    }

}
