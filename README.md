# OneDrive API Java SDK

This SDK allows for quick and easy usage of Microsoft OneDrive in Java 
applications. The [OneDrive RESTful API](https://dev.onedrive.com/index.htm) 
is thereby used to access Microsoft OneDrive. 

## Build

To build the jar invoke:

    mvn package 

## Usage

### Configuration file 

Before connecting to the OneDrive API with this SDK a configuration file
must be generated holding the various connection parameters used for 
authentication flow. The OneDrive API makes use of the OAUTH2.0 scheme for 
authentication and generating the access tokens. See 
[Authentication for the OneDrive API](https://dev.onedrive.com/auth/readme.htm)
for more background information

To generate the configuration file follow the understanding procedure:

1. Create a [Microsoft account](https://account.live.com/developers/applications/)
 and register your application. Minimally fill in the following properties 

    Basic Settings:
      Application name: <your-application-name>
      Language: <language-for-your-application>
    
    API Settings:
      Mobile or desktop client app: Yes
 
In the APP Settings section the "Client ID" and "Client Secret (v1)" for your 
application can be found. These are used in creating the authorization code to 
complete the OAUTH2.0 flow 

2. Generate the configuration file

Now run the following command to interactively create the configuration file:

    java -jar onedrive-sdk-${project.version}.jar    
 
When finished the file onedrive.properties is created. When using the OneDrive 
SDK this configuration file will be updated with the refresh token used by the
OAUTH2.0 flow. This file also holds other properties which may be changed to 
suit your needs. 


## Integration Testing

The integration tests are ran against an active OneDrive For this a valid
configuration for a OneDrive must be provided as created in the section above.
To run the test save the created configuration file under path:   
    'src/test/resources/onedrive-integrationtest.properties' 
    
first run the units by invoking:

    mvn test
 	 
then run the integration test by invoking:

    mvn failsafe:integration-test
 

*The integration test are not ran when building the package, this is a manual step.* 
 