# frdp-dao-rest

ForgeRock Demonstration Platform : Data Access Object : REST ... an implementation of the DAO interface using REST / JSON

`git clone https://github.com/ForgeRock/frdp-dao-rest.git`

# Requirements

The following items must be installed:

1. [Apache Maven](https://maven.apache.org/)
1. [Java Development Kit 8](https://openjdk.java.net/)

# Build

## Prerequisite:

The following items must be completed, in order:

1. [frdp-framework](https://github.com/ForgeRock/frdp-framework) ... clone / download then install using *Maven* (`mvn`)
1. [frdp-dao-mongo](https://github.com/ForgeRock/frdp-dao-mongo) ... clone / download then install using *Maven* (`mvn`)
1. [frdp-content-server](https://github.com/ForgeRock/frdp-content-server) ... clone / download then install using *Maven* (`mvn`)


Run *Maven* (`mvn`) processes to clean, compile and install the package:

```
mvn clean
mvn compile
mvn install
```

Packages are add to the user's home folder: 

`~/.m2/repository/com/forgerock/frdp/frdp-dao-rest`


# Test

This section covers how to use the `TestRestDataAccess.java` program which tests the REST Data Access Object (`RestDataAccess`) implementation.  A REST / JSON service must installed and accessible.  The **Content Server**, deployed from the `frdp-content-server` repository, provides a REST interface to JSON data.  The *test* applications uses this **Content Server**.  The *test* program will perform `create, read, search, replace, delete` operations.

## Update the `TestRestDataAccess.java` sample program:

1. Edit the test program \
`vi src/main/java/com/forgerock/frdp/dao/rest/TestRestDataAccess.java`
1. Set the `RestDataAccess` parameters: \
**Before:** \
`params.put(RestDataAccess.PARAM_PROTOCOL, "https");` \
`params.put(RestDataAccess.PARAM_HOST, "127.0.0.1");` \
`params.put(RestDataAccess.PARAM_PORT, "443");` \
`params.put(RestDataAccess.PARAM_PATH, "tomcat/content/rest/content-server/content");` \
**After:** \
`params.put(RestDataAccess.PARAM_PROTOCOL, "https");` \
`params.put(RestDataAccess.PARAM_HOST, "FQDN");` \
`params.put(RestDataAccess.PARAM_PORT, "443");` \
`params.put(RestDataAccess.PARAM_PATH, "TOMCAT_DEPLOYMENT/content/rest/content-server/content");` \
1. Build the project with *Maven* \
`mvn clean compile package install`

## Edit the `test.sh` script:

1. Set the `M2` variable to match your user folder name \
**Before:** \
`M2="/home/forgerock/.m2/repository"` \
**After:** \
`M2="/<USER_HOME_PATH>/.m2/repository"`
1. Run the `test.sh` script \
`sh ./test.sh` \
(sample test output below)

```
Dec 19, 2019 8:42:10 PM com.forgerock.frdp.dao.rest.RestDataAccess execute
WARNING: com.forgerock.frdp.dao.rest.RestDataAccess:execute: JSON Input is null or empty
====
==== Create output: error=false; state=SUCCESS; status='(NULL)'; params=none
==== Create json:   {"uid":"298a853d-60a3-4d65-9853-30bf8fdb1698"}
====
====
==== Create output: error=false; state=SUCCESS; status='(NULL)'; params=none
==== Create json:   {"uid":"cb7d8326-6a82-46a1-ac79-582dd0152980"}
====
====
==== Read output: error=false; state=SUCCESS; status='Found document'; params=none
==== Read json:   {"uid":"cb7d8326-6a82-46a1-ac79-582dd0152980","data":{"firstname":"Jack","organization":"CTU","title":"Agent","lastname":"Bauer","info":{"package":"com.forgerock.frdp.dao.rest","filename":"TestMongoDataAccess.java","classname":"TestMongoDataAccess","language":"java"}},"timestamps":{"created":"2019-12-19T20:42:12.861-0600"}}
====
====
==== Replace output: error=false; state=NOTEXIST; status='Not Found'; params=none
==== Replace json:   {}
====
====
==== Replace output: error=false; state=SUCCESS; status='Replaced document'; params=none
==== Replace json:   {}
====
====
==== Read output: error=false; state=SUCCESS; status='Found document'; params=none
==== Read json:   {"uid":"cb7d8326-6a82-46a1-ac79-582dd0152980","data":{"firstname":"Jack","organization":"CTU","comment":"Created from Test for MongoDataAccess class","title":"Agent","lastname":"Bauer","info":{"package":"com.forgerock.frdp.dao.rest","filename":"TestMongoDataAccess.java","classname":"TestMongoDataAccess","language":"java"},"status":"Updated"},"timestamps":{"created":"2019-12-19T20:42:12.861-0600","updated":"2019-12-19T20:42:12.901-0600"}}
====
====
==== Delete output: error=false; state=SUCCESS; status='Deleted document'; params=none
==== Delete json:   {}
====
====
==== Search output: error=false; state=SUCCESS; status='Found document'; params=none
==== Search json:   {"quantity":7,"results":["6c207df6-9331-4070-9790-58d8177aa134","3513f798-b39f-4a17-ac6c-855bd40cae35","dca1af46-cfea-4af6-98ba-3334036623c7","5831b709-78d7-448d-9a7d-a48c9f71f8d4","298a853d-60a3-4d65-9853-30bf8fdb1698","522747e3-dac9-4acf-8f9b-eb69e48fa1b5","8566f773-ff2a-4601-bb45-f3fe4b4f0cd5"]}
====
```