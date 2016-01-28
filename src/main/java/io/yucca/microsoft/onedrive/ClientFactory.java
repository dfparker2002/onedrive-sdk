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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Factory for creating a Jersey Client and ObjectMapper
 * 
 * <pre>
 * based on:
 * 1. http://www.theotherian.com/2013/08/jersey-client-2.0-httpclient-timeouts-max-connections.html
 * 2. http://www.javaworld.com/article/2824163/application-performance/stability-patterns-applied-in-a-restful-architecture.html
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
     * Create a pooled Jersey client using the Apache HTTP connector providor.
     * 
     * <pre>
     * ClientProperties.READ_TIMEOUT is set through configuration
     * ClientProperties.CONNECT_TIMEOUT is set through configuration
     * 
     * Pooled connection manager max connections = 25
     * Pooled connection manager max connections per route = 5
     * 
     * Using the ApacheConnectorProvider fails for MultiPart uploads because in 
     * this connector headers may not be modified @see <a href="https://jersey.java.net/documentation/latest/client.html#d0e4832">5.5 Client Transport Connectors</a>
     * </pre>
     * <p>
     * Support for TLS/SSL comes default from the client implementation, also no
     * logic is needed to allow self-signed certificates.
     * 
     * @param configuration OneDriveConfiguration
     * @param providers Object... providers
     * @return Client
     */
    public static Client create(OneDriveConfiguration configuration,
                                Object... providers) {
        ClientConfig clientConfig = new ClientConfig(providers);
        /**
         * Allow restricted headers to be set. Prevents a warning in
         * {@link UploadResumableAction} which specifies Content-Length
         * {@link https://jersey.java.net/documentation/latest/client.html#d0e4832}
         */
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(25);
        connectionManager.setDefaultMaxPerRoute(5);
        clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER,
                              connectionManager);
        LOG.debug("Client pooling values set, maximum: {}, per-route: {}", 25,
                  5);

        // see javadoc for reason of disabling
        // clientConfig.connectorProvider(new ApacheConnectorProvider());
        RequestConfig reqConfig = RequestConfig.custom()
            .setConnectTimeout(configuration.getConnectionTimeout())
            .setSocketTimeout(configuration.getReadTimeout())
            .setConnectionRequestTimeout(200).build();
        clientConfig.property(ApacheClientProperties.REQUEST_CONFIG, reqConfig);
        LOG.debug("Client timeout values set, connection timeout: {}, read timeout: {}",
                  configuration.getConnectionTimeout(),
                  configuration.getReadTimeout());

        clientConfig.register(OneDriveContentMessageBodyReader.class);
        clientConfig.register(OneDriveContentMessageBodyWriter.class);
        clientConfig.register(MultiPartWriter.class);
        clientConfig.register(JacksonFeature.class);
        return ClientBuilder.newBuilder().withConfig(clientConfig).build();
    }

    /**
     * Create an ObjectMapper
     * 
     * @param jacksonProvider JacksonJsonProvider
     * @return ObjectMapper
     */
    public static ObjectMapper createMapper(JacksonJsonProvider jacksonProvider) {
        ObjectMapper mapper = new ObjectMapper();
        // prevents inclusion of null values on serialization
        mapper.setSerializationInclusion(Include.NON_NULL);
        // indent output on serialization
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // prevents mapping failure on unknown properties
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // allows a single value to be deserialized as an array/list
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        jacksonProvider.setMapper(mapper);
        return mapper;
    }
}
