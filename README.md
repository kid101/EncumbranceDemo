# cordacon
This CordApp caches a file using a `Corda Service` which contains the list of names of parties to which some data needs to be sent. 
In our case the data is just a simple hello

The Flow then loads the cached file sends data or 'Hello' in our case to parties mentioned in the file.


**Step 1**: From the Project DIR

do a **gradle deployNodes** to create the nodes.

**Step 2**: From the Project DIR

Do a **gradle createServer** to create the BootJar in demo-server project. 

**Step 3**: Start the Nodes

Navigate inside `./build/node/` and `runnodes` to start the node

**Step 4**: Start the WebServer

Navigate inside `./build/node/` and `runserver` to start the servers

**Related Info**

PartyA is listening on localhost:8080, PartyB is listenting on localhost:8081, and PartyC is listening on localhost:8082.

Only PartyA has privilege to run the service. demo-service cordapp is deployed on PartyA only.
In build/node/PartyA/ you will find 2 folders. Cache and Response:
Cache is the CacheDir where the okHttp Cache is saved. Response is the Folder where the partyList.txt file gets saved. 

Ethereal Service is responsible to get the List of Parties from the outside out. To which `Hello` should be sent.

Downloads file from link: https://raw.githubusercontent.com/kid101/cordacon/master/sayhelloto
Location can be changed to point to some other location.

After downloading the file. Flow Loads it and sends hello to parties mentioned in the file.

**API**

* `demo/hello` to test the server is working
* `demo/me` to get the node's identity
* `demo/sayHelloTo` to send hello to the parties mentioned in the partyList.txt
* `demo/getAllHello/{pageNumber}` to view all the hello's

**Common Issues**:

If facing access issues to jar or shell script or log file: `chmod` to give appropriate permissions.
