# OneDrive API Java SDK

This SDK allows for quick and easy usage of Microsoft OneDrive in Java 
applications. The [OneDrive RESTful API](https://dev.onedrive.com/index.htm) 
is thereby used to access Microsoft OneDrive. 

## Build

To build the jar invoke:

    mvn package 

## Usage

### Create a Configuration File 

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
complete the OAuth2.0 flow 

2. Generate the configuration file

Now run the following command to interactively create the configuration file:

    java -jar onedrive-sdk-${project.version}.jar    
 
When finished the file onedrive.properties is created. When using the OneDrive 
SDK this configuration file will be updated with the refresh token used by the
OAUTH2.0 flow. This file also holds other properties which may be changed to 
suit your needs. 


## Example  

To use the SDK a valid configuration file must exist. Creating this configuration 
is explained in [Creating a Configuration](#create-a-configuration-file)

### Connecting to OneDrive

A connection to the OneDrive API is represented by an OneDriveAPIConnection 
instance. In constructing this object the OneDrive, the configuration file 
holding the various parameters for a User Drive or App Drive are passed.  

This connection instance is the startpoint in performing various actions on 
the OneDrive API.
  
    OneDriveConfiguration configuration = ConfigurationUtil.read(CONFIGURATIONFILE);
    OneDriveAPIConnection api = new OneDriveAPIConnection(configuration);
   
### Getting Drive information

To use a drive, first request an OneDrive instance, this can be the default 
or specific drive identified by id.

    OneDrive drive = OneDrive.defaultDrive(api);
    System.out.println("Hello user: " + drive.getUser().getDisplayName());   

or by drive identifier 
 
    OneDrive drive = OneDrive.byDriveId(api, "0123456789abc");
    System.out.println("Hello user: " + drive.getUser().getDisplayName());
    
### Listing all folders in the Drive

Requesting all all folders in a drive is done as following.
 
    OneDrive drive = OneDrive.defaultDrive(api);
    for (OneDriveItem item : drive.listChildren()) {
        if (item instanceof OneDriveFolder) {
           System.out.format(" %s", item.getItem().getName());
        }
    }    

For more information see the examples in "src/examples/java or the documentation.

## Integration Testing

The integration tests are ran against an active OneDrive For this a valid
configuration for a OneDrive must be provided as created in the section above.
To run the test save the created configuration file under path:   
    'src/test/resources/onedrive-integrationtest.properties' 
    
first run the units by invoking:

    mvn test
 	 
then run the integration tests by invoking:

    mvn failsafe:integration-test
 
to run a single integration test and skip unit tests invoke:

    mvn -DskipUTs=true -Dit.test=DriveActionIT verify

*The integration test are not ran when building the package, this is a manual step.* 
