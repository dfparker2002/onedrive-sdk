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

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents an authenticated connection to the OneDrive API
 *
 * @author yucca.io
 */
public interface OneDriveAPIConnection {

    /**
     * Get authorized client.
     * 
     * @return Client
     */
    Client getClient();

    /**
     * Determine if client is authorized
     * 
     * @return true if authorized
     */
    boolean isAuthorized();

    /**
     * Get WebTarget based on authorized client.
     * <p>
     * TODO determine URL based on configuration parameter
     * </p>
     * 
     * @return WebTarget
     */
    WebTarget webTarget();

    /**
     * Get WebTarget based on authorized client.
     * 
     * @param uri URI to target
     * @return WebTarget
     */
    WebTarget webTarget(URI uri);

    /**
     * Closes the client with all webTargets
     */
    void close();

    /**
     * Logout from OneDrive API
     */
    void logOut();

    /**
     * Get ObjectMapper
     * 
     * @return ObjectMapper
     */
    ObjectMapper getMapper();

}
