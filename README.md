# OneDrive API Java SDK

## Build

To build the jar use:

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
OAUTH2.0 flow. This file also holds some other properties which may be changed
to suit your needs. 


## Integration Testing

The integration tests are runned against a active OneDrive. For this a valid
configuration for a OneDrive must be provided as created in the section above.
To run the test save the created configuration file under path:   
    'src/test/resources/onedrive-integrationtest.properties' 
    
and invoke:
 
     mvn failsafe:integration-test 


## Examples



## TODO

### OneDriveAPIConnection

Upload: multipart upload
Upload: URL upload
Thumbnails: retreive and uploading of (custom) thumbnails
Throttling: https://dev.onedrive.com/README.htm

### ConfigurationUtil

Multithreaded access: implement lock/unlock when multithreaded access is active

### Client

Fix header warning on resumable upload
Sep 02, 2015 4:35:51 PM org.glassfish.jersey.client.HttpUrlConnector setOutboundHeaders
WARNING: Attempt to send restricted header(s) while the [sun.net.http.allowRestrictedHeaders] system property not set. Header(s) will possibly be ignored.


Set a retry handler to handle read timeouts java.net.SocketTimeoutException
https://java.net/jira/browse/JERSEY-2139?jql=text%20~%20%22retry%20handler%22
http://www.nailedtothex.org/roller/kyle/entry/articles-test-wiremockunstable


## Publish

http://datumedge.blogspot.nl/2012/05/publishing-from-github-to-maven-central.html
https://veithen.github.io/2013/05/26/github-bintray-maven-release-plugin.html

## Mapper

http://www.baeldung.com/jackson-serialize-enums
http://gotoanswer.com/?q=Jersey+2+LoggingFilter
https://dzone.com/articles/whats-new-jax-rs-20
http://www.chrisellsworth.com/blogs/devblog/archive/2006/10/31/Java-Concurrency-API-Example.aspx
https://stackoverflow.com/questions/14410344/jersey-rest-support-resume-media-streaming

## InputStream

http://usabilityetc.com/articles/size-input-streams/

## TLS HTTP Apache
http://w3facility.org/question/how-to-select-cipher-suites-in-jersey-2-using-apacheconnector/
https://github.com/jersey/jersey/blob/master/examples/sse-item-store-webapp/src/test/java/org/glassfish/jersey/examples/sseitemstore/ItemStoreResourceTest.java

## BITS

### BITS Upload Protocol
https://msdn.microsoft.com/en-us/library/aa362828(v=vs.85).aspx

### Uploading large files to OneDrive by using BITS
https://msdn.microsoft.com/en-us/library/dn858877.aspx?f=255&MSPPError=-2147217396
