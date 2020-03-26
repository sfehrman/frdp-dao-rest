# frdp-dao-rest

ForgeRock Demonstration Platform : Data Access Object : REST ... an implementation of the DAO interface using REST / JSON

`git clone https://github.com/ForgeRock/frdp-dao-rest.git`

# Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

# License

[MIT](/LICENSE)

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


Run *Maven* (`mvn`) processes to: clean, compile and install the package:

```bash
mvn clean compile install
```

Packages are add to the user's home folder: 

`find ~/.m2/repository/com/forgerock/frdp/frdp-dao-rest`

```bash
/Users/scott.fehrman/.m2/repository/com/forgerock/frdp/frdp-dao-rest
/Users/scott.fehrman/.m2/repository/com/forgerock/frdp/frdp-dao-rest/1.1.0
/Users/scott.fehrman/.m2/repository/com/forgerock/frdp/frdp-dao-rest/1.1.0/frdp-dao-rest-1.1.0.pom
/Users/scott.fehrman/.m2/repository/com/forgerock/frdp/frdp-dao-rest/1.1.0/frdp-dao-rest-1.1.0.jar
/Users/scott.fehrman/.m2/repository/com/forgerock/frdp/frdp-dao-rest/1.1.0/_remote.repositories
/Users/scott.fehrman/.m2/repository/com/forgerock/frdp/frdp-dao-rest/maven-metadata-local.xml
```

# Test

This section covers how to use the `TestRestDataAccess.java` program which tests the REST Data Access Object (`RestDataAccess`) implementation.  A REST / JSON service must installed and accessible.  The **Content Server**, deployed from the `frdp-content-server` repository, provides a REST interface to JSON data.  The *test* applications uses this **Content Server**.  The *test* program will perform `create, read, search, replace, delete` operations.

## Update the `TestRestDataAccess.java` sample program:

1. Edit the test program \
`vi src/main/java/com/forgerock/frdp/dao/rest/TestRestDataAccess.java`
1. Make sure the following variables match your deployment for the **Content Server**: \
`PROTOCOL`: Either `http` or `https` \
`HOST`: The Fully Qualified Domain Name of where the Content Server is running *("127.0.0.1" might work)* \
`PORT`: Port, on the HOST, that is listening for connections\
`PATH`: Relative URL for the Content Server end-point that is handing the MongoDB database: `content-server` and the MongoDB collection: `content`\
**Before:** \
`private static final String PROTOCOL = "https";` \
`private static final String HOST = "uma.example.com";` \
`private static final String PORT = "443";` \
`private static final String PATH = "content-server/rest/content-server/content";` \
**After:** \
`private static final String PROTOCOL = "https";` \
`private static final String HOST = "FQDN";` \
`private static final String PORT = "443";` \
`private static final String PATH = "content-server/rest/content-server/content";` \
1. Build the project with *Maven* \
`mvn clean compile install`

## Edit the `test.sh` script:

1. Set the `M2` variable to match your user folder name \
**Before:** \
`M2="/home/forgerock/.m2/repository"` \
**After:** \
`M2="/<USER_HOME_PATH>/.m2/repository"`
1. Run the `test.sh` script \
`sh ./test.sh` \
(sample test output below)

```bash
Mar 24, 2020 3:11:04 PM com.forgerock.frdp.dao.rest.RestDataAccess execute
WARNING: com.forgerock.frdp.dao.rest.RestDataAccess:execute: No base target, required attribute 'uri' is empty
====
==== FAIL TEST     : create 0, DAO constructor, missing uri test
==== URI Location  : null
==== Create output : error=true; state=FAILED; status='com.forgerock.frdp.dao.rest.RestDataAccess:execute: No base target, required attribute 'uri' is empty'; params=none
==== Create json   : {}
====
====
==== SUCCESS TEST  : create 1, DAO constructor, with uri
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/a40484be-e1c1-4d40-891e-99057b2e1339
==== Create output : error=false; state=SUCCESS; status='Response: Created'; params=none
==== Create json   : {"uri":"https:\/\/idp.frdpcloud.com\/content-server\/rest\/content-server\/content\/a40484be-e1c1-4d40-891e-99057b2e1339"}
====
Mar 24, 2020 3:11:05 PM com.forgerock.frdp.dao.rest.RestDataAccess execute
WARNING: com.forgerock.frdp.dao.rest.RestDataAccess:execute: JSON Input is null or empty
====
==== FAIL TEST     : create 2, DAO constructor params, null input
==== URI Location  : null
==== Create output : error=true; state=FAILED; status='com.forgerock.frdp.dao.rest.RestDataAccess:execute: JSON Input is null or empty'; params=none
==== Create json   : {}
====
====
==== SUCCESS TEST  : create 3, DAO constructor params, with query parameters
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/2ec9d6ec-a941-4910-a6dd-356d0981fd25
==== Create output : error=false; state=SUCCESS; status='Response: Created'; params=none
==== Create json   : {"uri":"https:\/\/idp.frdpcloud.com\/content-server\/rest\/content-server\/content\/2ec9d6ec-a941-4910-a6dd-356d0981fd25"}
====
====
==== SUCCESS TEST  : create 4, DAO constructor params, with headers
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/d73f6ca7-87fa-4678-82d8-1949600559c2
==== Create output : error=false; state=SUCCESS; status='Response: Created'; params=none
==== Create json   : {"uri":"https:\/\/idp.frdpcloud.com\/content-server\/rest\/content-server\/content\/d73f6ca7-87fa-4678-82d8-1949600559c2"}
====
====
==== SUCCESS TEST  : create 5, DAO constructor params, with cookies
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/a5316f3a-3134-4462-9079-872116fc5ea5
==== Create output : error=false; state=SUCCESS; status='Response: Created'; params=none
==== Create json   : {"uri":"https:\/\/idp.frdpcloud.com\/content-server\/rest\/content-server\/content\/a5316f3a-3134-4462-9079-872116fc5ea5"}
====
====
==== SUCCESS TEST  : create 6, DAO default constructor
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada
==== Create output : error=false; state=SUCCESS; status='Response: Created'; params=none
==== Create json   : {"uri":"https:\/\/idp.frdpcloud.com\/content-server\/rest\/content-server\/content\/bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada"}
====
====
==== FAIL TEST     : read 1, DAO constructor params, bad uri
==== URI Location  : http://bad.example.com/app/rest/content/BadId123
==== Read output   : error=true; state=WARNING; status='Could not parse response entity: null'; params=none
==== Read json     : {}
====
====
==== SUCCESS TEST  : read 2, DAO constructor params, good uri
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada
==== Read output   : error=false; state=SUCCESS; status='Found document'; params=none
==== Read json     : {"uid":"bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada","data":{"firstname":"Jack","organization":"CTU","title":"Agent","lastname":"Bauer","info":{"package":"com.forgerock.frdp.dao.rest","filename":"TestMongoDataAccess.java","classname":"TestMongoDataAccess","language":"java"}},"timestamps":{"created":"2020-03-24T15:11:05.820-0500"}}
====
====
==== FAIL TEST     : replace 1, DAO constructor params, bad uri
==== URI Location  : http://bad.example.com/app/rest/content/BadId123
==== Replace output: error=false; state=SUCCESS; status='Replaced document'; params=none
==== Replace json  : {}
====
====
==== SUCCESS TEST  : replace 2, DAO constructor params
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada
==== Replace output: error=false; state=SUCCESS; status='Replaced document'; params=none
==== Replace json  : {}
====
====
==== SUCCESS TEST  : read 3, DAO constructor params, after a replace
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada
==== Read output   : error=false; state=SUCCESS; status='Found document'; params=none
==== Read json     : {"uid":"bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada","data":{"firstname":"Jack","organization":"CTU","comment":"Created from Test for MongoDataAccess class","title":"Agent","lastname":"Bauer","info":{"package":"com.forgerock.frdp.dao.rest","filename":"TestMongoDataAccess.java","classname":"TestMongoDataAccess","language":"java"},"status":"Updated"},"timestamps":{"created":"2020-03-24T15:11:05.820-0500","updated":"2020-03-24T15:11:06.647-0500"}}
====
====
==== SUCCESS TEST  : delete 1, DAO constructor params
==== URI Location  : https://idp.frdpcloud.com/content-server/rest/content-server/content/bd3ac0c2-eb62-43ab-9ca8-3b3efa0a7ada
==== Delete output : error=false; state=SUCCESS; status='Deleted document'; params=none
==== Delete json   : {}
====
====
==== SUCCESS TEST : search 1, DAO constructor params
==== Search output: error=false; state=SUCCESS; status='Found document'; params=none
==== Search json  : {"quantity":4,"results":["a40484be-e1c1-4d40-891e-99057b2e1339","2ec9d6ec-a941-4910-a6dd-356d0981fd25","d73f6ca7-87fa-4678-82d8-1949600559c2","a5316f3a-3134-4462-9079-872116fc5ea5"]}
====
====
==== SUCCESS TEST : search 2, DAO default constructor
==== URI Base     : https://idp.frdpcloud.com:443/content-server/rest/content-server/content
==== Search output: error=false; state=SUCCESS; status='Found document'; params=none
==== Search json  : {"quantity":4,"results":["a40484be-e1c1-4d40-891e-99057b2e1339","2ec9d6ec-a941-4910-a6dd-356d0981fd25","d73f6ca7-87fa-4678-82d8-1949600559c2","a5316f3a-3134-4462-9079-872116fc5ea5"]}
====
```