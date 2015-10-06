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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Factory to create a Jersey Client and ObjectMapper
 * 
 * <pre>
 * based on:
 * http://www.theotherian.com/2013/08/jersey-client-2.0-httpclient-timeouts-max-
 * connections.html
 * </pre>
 * 
 * @author yucca.io
 */
public final class ClientFactory {

    private static final Logger LOG = LoggerFactory
        .getLogger(ClientFactory.class);

    private ClientFactory() {
    }

    /**
     * Create a pooled Jersey client
     * 
     * <pre>
     * max connections = 100
     * max connections per route = 20
     * </pre>
     * 
     * @param configuration OneDriveConfiguration
     * @param providers Object... providers
     * @return Client
     */
    public static Client create(OneDriveConfiguration configuration,
                                Object... providers) {
        ClientConfig clientConfig = new ClientConfig(providers);
        clientConfig.property(ClientProperties.READ_TIMEOUT,
                              configuration.getReadTimeout());
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT,
                              configuration.getConnectionTimeout());
        LOG.debug("Client timeout values set, connection timeout: {}, read timeout: {}",
                  configuration.getConnectionTimeout(),
                  configuration.getReadTimeout());

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        LOG.debug("Client pooling values set, maximum: {}, per-route: {}", 100,
                  20);

        /**
         * Allow restricted headers to be set. Prevents a warning in
         * {@link UploadResumableAction} which specifies Content-Length
         * {@link https://jersey.java.net/documentation/latest/client.html#d0e4832}
         */
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        Client client = ClientBuilder.newBuilder().withConfig(clientConfig)
            .build();
        client.register(OneDriveContentMessageBodyReader.class);
        client.register(OneDriveContentMessageBodyWriter.class);
        client.register(MultiPartWriter.class);
        client.register(JacksonFeature.class);
        client.property(ApacheClientProperties.CONNECTION_MANAGER,
                        connectionManager);
        clientConfig.connectorProvider(new ApacheConnectorProvider());
        return client;
    }

    /**
     * Create an ObjectMapper
     * 
     * @param jacksonProvider JacksonJaxbJsonProvider
     * @return ObjectMapper
     */
    public static ObjectMapper createMapper(JacksonJaxbJsonProvider jacksonProvider) {
        ObjectMapper mapper = new ObjectMapper();
        // prevents inclusion of null values on serialization
        mapper.setSerializationInclusion(Include.NON_NULL);
        // indent output on serialization
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // prevents mapping failure on unknown properties
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jacksonProvider.setMapper(mapper);
        return mapper;
    }
}
