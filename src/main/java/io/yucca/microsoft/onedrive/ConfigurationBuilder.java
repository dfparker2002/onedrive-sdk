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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration.ConfigurationException;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a configuration file interactive from the commandline. Requesting an
 * authorization code used in the OAuth2 flow to acquire the authorization and
 * refresh tokens for usage of the OneDrive API.
 *
 * @author yucca.io
 */
public class ConfigurationBuilder {

    private static final Logger LOG = LoggerFactory
        .getLogger(ConfigurationBuilder.class);

    private static ConfigurationBuilder builder;

    private static final String CONFIGURATION_FILE = "onedrive.properties";

    private final OneDriveConfiguration configuration;

    public static void main(String[] args) throws IOException {
        builder = new ConfigurationBuilder();
        builder.build(CONFIGURATION_FILE);
    }

    public ConfigurationBuilder() {
        this.configuration = new OneDriveConfiguration(CONFIGURATION_FILE);
    }

    public void build(String configurationFile) throws IOException {
        if (exists(configurationFile) == false) {
            System.exit(0);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            LOG.info("********************************************************");
            LOG.info("*                                                      *");
            LOG.info("* Lets start creating the OneDrive configuration file. *");
            LOG.info("*                                                      *");
            LOG.info("********************************************************");
            LOG.info("1. Enter the Client ID created for your application.");
            System.out.print(">> ");
            configuration.setClientId(br.readLine());

            LOG.info("2. Enter the Client secret (v1) created for your application.");
            System.out.print(">> ");
            configuration.setClientSecret(br.readLine());
            ClientIdentifier id = new ClientIdentifier(configuration
                .getClientId(), configuration.getClientSecret());
            OAuth2CodeGrantFlow flow = OneDriveOAuthHelper
                .buildFlow(id, OneDriveOAuthHelper.DEFAULT_SCOPE);

            LOG.info("3. Enter the following URL in a browser and accept the permissions question.");
            System.out.println(flow.start());
            LOG.info(">> [hit a key to proceed]");
            br.readLine();

            LOG.info("4. Now Paste the redirection URL code you received in the browser to extract the authorization code.");
            System.out.print(">> ");

            configuration.setAuthorizationCode(OneDriveOAuthHelper
                .extractAuthorizationCodeFromTokenURL(br.readLine()));
            LOG.info("Succesfully extracted the authorization code from the URL.");
            ConfigurationUtil.save(configuration);
            LOG.info("The OneDrive configuration file was witten sucessfully to: "
                     + configurationFile);
        } catch (IOException e) {
            LOG.error("Failed to read input.", e);
        } catch (ConfigurationException e) {
            LOG.error("Failed to build configuration.", e);
        } finally {
            br.close();
        }
    }

    private boolean exists(String configurationFile) {
        File file = new File(configurationFile);
        if (file.exists()) {
            LOG.error("The configuration file: " + configurationFile
                      + " already exists, delete the existing configuration before building a new one.");
            return false;
        }
        return true;
    }

}
