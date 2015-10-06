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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yucca.microsoft.onedrive.util.URLHelper;

/**
 * Represents a authenticated session to the OneDrive RESTful API. Provides the
 * means to authenticate and authorize to the API using OAauth2 mechanism.
 * <p>
 * https://dev.onedrive.com/auth/msa_oauth.htm
 * https://dev.onedrive.com/auth/aad_oauth.htm
 * http://www.ibm.com/developerworks/library/se-oauthjavapt3/index.html
 * https://msdn.microsoft.com/en-us/library/dn659750.aspx
 * https://msdn.microsoft.com/en-us/library/azure/dn645543.aspx
 * http://www.hascode.com/2013/12/jax-rs-2-0-rest-client-features-by-example/
 * </p>
 * 
 * @author yucca.io
 */
public class OneDriveSession {

    private static Logger LOG = LoggerFactory.getLogger(OneDriveSession.class);

    public static final String CLIENT_ID = "client_id";

    public static final String CLIENT_SECRET = "client_secret";

    public static final String REDIRECT_URI = "redirect_uri";

    public static final String CODE = "code";

    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String STATE = "state";

    /**
     * The login authentication URI
     */
    public static final String ONEDRIVE_OAUTH20_AUTH_URI = "https://login.live.com/oauth20_authorize.srf";

    /**
     * The token redeem URI
     */
    public static final String ONEDRIVE_OAUTH20_TOKEN_URI = "https://login.live.com/oauth20_token.srf";

    /**
     * Default redirection URI
     */
    public static final String DEFAULT_REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf";

    /**
     * The logout URI
     */
    public static final String ONEDRIVE_OAUTH20_LOGOUT = "https://login.live.com/oauth20_logout.srf?client_id={client_id}&redirect_uri={redirect_uri}";

    /**
     * Default authentication scopes
     */
    public static final AuthenticationScope[] DEFAULT_SCOPE = { AuthenticationScope.WL_SIGNIN,
                                                                AuthenticationScope.WL_OFFLINE_ACCESS,
                                                                AuthenticationScope.ONEDRIVE_READWRITE,
                                                                AuthenticationScope.ONEDRIVE_APPFOLDER };

    private final String clientId;

    private final String clientSecret;

    private final ClientIdentifier clientIdentifier;

    private final AuthenticationScope[] scope;

    private final String authorizationCode;

    private final Client client;

    private final OneDriveConfiguration configuration;

    private OAuth2CodeGrantFlow flow;

    private String state;

    private TokenResult accessToken;

    private long lastRefresh;

    /**
     * OneDriveOAuthHelper
     * 
     * @param configuration OneDriveConfiguration
     * @param client Client client in working with the OneDriveAPI
     */
    public OneDriveSession(OneDriveConfiguration configuration, Client client) {
        LOG.info("Initializing OneDrive session");
        this.configuration = configuration;
        this.client = client;
        this.clientId = configuration.getClientId();
        this.clientSecret = configuration.getClientSecret();
        this.clientIdentifier = new ClientIdentifier(clientId, clientSecret);
        this.authorizationCode = configuration.getAuthorizationCode();
        this.scope = DEFAULT_SCOPE;
        initFlow(client);
        initTokenResult(configuration.getRefreshToken());
    }

    /**
     * Initialize the OAuth2 flow
     * 
     * @param client
     */
    private void initFlow(Client client) {
        LOG.info("Initializing authorizing flow to the OneDrive API: {}",
                 ONEDRIVE_OAUTH20_AUTH_URI);
        if (flow == null) {
            this.flow = buildFlow(clientIdentifier, scope);
        }
        String authorizationUri = flow.start();
        setFlowState(authorizationUri);
        if (authorizationCode == null || authorizationCode.isEmpty()) {
            throw new IllegalStateException("No authorization code was provided, which is needed to acquire an accessToken for the OneDrive API.\n"
                                            + "Write the authorization code in the configuration file or build this configuration with the commandline utility.\n");
        }
    }

    /**
     * Initialise TokenResult with refresh_token
     * 
     * @param refreshToken String
     */
    private void initTokenResult(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            Map<String, Object> props = new LinkedHashMap<>();
            props.put(REFRESH_TOKEN, refreshToken);
            this.accessToken = new TokenResult(props);
        }
    }

    /**
     * Acquire state value from authorizationUri used in requesting an
     * accessToken
     * 
     * @param authorizationUri String
     */
    private void setFlowState(String authorizationUri) {
        try {
            this.state = URLHelper.splitQuery(new URL(authorizationUri))
                .get(STATE);
        } catch (MalformedURLException e) {
            this.state = "";
        }
    }

    /**
     * Authorize and register the OAuth2 feature on the client
     */
    private void redeemAccessToken() {
        LOG.info("Authorizing to OneDrive API and requesting an accessToken by url: {}",
                 ONEDRIVE_OAUTH20_TOKEN_URI);
        this.accessToken = flow.finish(authorizationCode, state);
        this.lastRefresh = System.currentTimeMillis();
        saveConfiguration();
        LOG.debug("Acquired a new accessToken");
        LOG.debug("Authorization to OneDrive API succeeded");
    }

    /**
     * Determine if client is authorized
     * 
     * @return true if
     */
    private boolean isAuthorized() {
        return hasAccessToken();
    }

    /**
     * Request an AccessToken or refresh if expired.
     * <p>
     * XXX Explicitly package local for testing, create unit test
     * </p>
     */
    void requestAccessToken() {
        if (isAuthorized() == false) {
            if (hasRefreshToken() == false) {
                redeemAccessToken();
            } else {
                refreshAccessToken();
            }
            this.client.register(flow.getOAuth2Feature());
            LOG.debug("OAuth2 feauture registred on client");
        } else if (hasRefreshToken() && isTokenExpired()) {
            refreshAccessToken();
        }
    }

    /**
     * Refresh an AccessToken if expired
     */
    private void refreshAccessToken() {
        LOG.debug("Refreshing accessToken");
        this.accessToken = flow
            .refreshAccessToken(accessToken.getRefreshToken());
        this.lastRefresh = System.currentTimeMillis();
        this.configuration.setRefreshToken(accessToken.getRefreshToken());
        saveConfiguration();
        LOG.debug("Refreshed accessToken, valid for {}s.",
                  accessToken.getExpiresIn());
    }

    /**
     * Determine if token has expired
     * 
     * @return true if token is expired
     */
    private boolean isTokenExpired() {
        if (hasExpiration() == false) {
            return true;
        }
        long now = System.currentTimeMillis();
        if ((now - lastRefresh) > (accessToken.getExpiresIn() * 1000)) {
            LOG.info("AccessToken has expired.");
            return true;
        }
        return false;
    }

    /**
     * Determine if a accessToken is available
     * 
     * @return boolean
     */
    private boolean hasAccessToken() {
        return (accessToken != null && accessToken.getAccessToken() != null);
    }

    /**
     * Determine if a refreshToken is available
     * 
     * @return boolean
     */
    private boolean hasRefreshToken() {
        return (accessToken != null && accessToken.getRefreshToken() != null);
    }

    /**
     * Determine if an expiration time is set
     * 
     * @return boolean true if set
     */
    private boolean hasExpiration() {
        return (accessToken.getExpiresIn() != null);
    }

    /**
     * Logout from the OneDrive API and clear auth tokens
     */
    public void logOut() {
        LOG.info("Logging out from OneDrive API");
        Response response = client.target(ONEDRIVE_OAUTH20_LOGOUT)
            .resolveTemplate(CLIENT_ID, clientId)
            .resolveTemplate(REDIRECT_URI, DEFAULT_REDIRECT_URI)
            .request(MediaType.TEXT_PLAIN_TYPE).get();
        if (response.getStatus() == 200) {
            LOG.info("Succesfully logged from OneDrive API");
        } else {
            throw new OneDriveException("Failure logging out from OneDrive API: "
                                        + response.getStatusInfo()
                                            .getReasonPhrase());
        }
    }

    /**
     * Get the TokenResult
     * 
     * @return TokenResult
     */
    public TokenResult getAccessToken() {
        return accessToken;
    }

    /**
     * Get authorized client
     * 
     * @return Client
     */
    public Client getClient() {
        requestAccessToken();
        return client;
    }

    /**
     * @param configurationFile String
     */
    void saveConfiguration() {
        try {
            ConfigurationUtil.save(configuration);
        } catch (ConfigurationException e) {
            throw new OneDriveException("Failure saving configuration", e);
        }
    }

    /**
     * Build an OAuth2CodeGrantFlow
     * 
     * @param clientIdentifier ClientIdentifier
     * @param scopes AuthenticationScope[]
     * @return OAuth2CodeGrantFlow
     */
    public static OAuth2CodeGrantFlow buildFlow(ClientIdentifier clientIdentifier,
                                                AuthenticationScope[] scopes) {
        OAuth2CodeGrantFlow.Builder<?> builder = OAuth2ClientSupport
            .authorizationCodeGrantFlowBuilder(clientIdentifier,
                                               ONEDRIVE_OAUTH20_AUTH_URI,
                                               ONEDRIVE_OAUTH20_TOKEN_URI);
        return builder.redirectUri(DEFAULT_REDIRECT_URI)
            .scope(AuthenticationScope.getScopes(scopes)).build();
    }

    /**
     * Extract the authorization code from the tokenURL (redirected)
     * 
     * @param tokenURL String tokenURL
     * @return String authorization code
     */
    public static String extractAuthorizationCodeFromTokenURL(String tokenURL) {
        try {
            Map<String, String> params = URLHelper
                .splitQuery(new URL(tokenURL));
            String code = params.get(CODE);
            if (code == null) {
                throw new OneDriveException("The token URL does not contain an authorization code");
            }
            return code;
        } catch (MalformedURLException e) {
            throw new OneDriveException("The token URL is invalid", e);
        }
    }

}
