# cordacon

Step 1:

Use **gradle deployNodes** to create the nodes. 

Step 2:
Do a **gradle createServer**.now execute **./build/node/runserver** to start the server

Only PartyA has privilege to run the service. so in build/node/PartyA/ you will find 2 foldes. Cache and Response:
Cache is the CacheDir where the okHttp Cache is saved. Response is the Folder where the BitCoin Readme.txt file gets saved. 
