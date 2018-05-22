# cordacon

Step 1: From the Project DIR

do a **gradle deployNodes** to create the nodes. Navigate inside `./build/node/` and runserver

Step 2: From the Project DIR

Do a **gradle createServer** to create the BootJar in demo-server project. execute`./build/node/runserver` to start the servers

Only PartyA has privilege to run the service (On Conditional Property Used. looks for `active.service`set to `true` in JVM args to Activate the Service). 
In build/node/PartyA/ you will find 2 folders. Cache and Response:
Cache is the CacheDir where the okHttp Cache is saved. Response is the Folder where the partyList.txt file gets saved. 

Ethereal Service is responsible to get the List of Parties from the outside out. To which `Hello` should be sent.

Downloads file from here:https://raw.githubusercontent.com/kid101/cordacon/master/sayhelloto
Location can be changed to point to some other location.

After downloading the file. Flow Loads it and send hello to parties mentioned in the file.
