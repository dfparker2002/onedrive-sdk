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

/**
 * Parameters used in accessing the OneDrive API
 *
 * @author yucca.io
 */
public class OneDriveConfiguration {

    public static final int READ_TIMEOUT_DEFAULT = 40000;

    public static final int CONNECTION_TIMEOUT_DEFAULT = 4000;

    public static final boolean DEBUG_LOGGING_DEFAULT = false;

    private final String configurationFile;

    private String platform;

    private String clientId;

    private String clientSecret;

    private String authorizationCode;

    private String refreshToken;

    private String deltaToken;

    private int readTimeout = READ_TIMEOUT_DEFAULT;

    private int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;

    private boolean debugLogging = DEBUG_LOGGING_DEFAULT;

    public OneDriveConfiguration(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDeltaToken() {
        return deltaToken;
    }

    public void setDeltaToken(String deltaToken) {
        this.deltaToken = deltaToken;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isDebugLogging() {
        return debugLogging;
    }

    public void setDebugLogging(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }
}
