# Napster-Mock-Project-
A simple version of the Napster software created within vanilla Java. 
(On hiatus)

FILES FINISHED: 

client1-3: used as the interface menu for the user. Relies on cmd commands as inputs. 
- Multiple client files are utilized in order to test simultaneous server receiving multiple, different requests from clients simultaneously
- Initializes threadpools used for p2p and client-server interaction
- Communicates with threads in order for commands to run in parallel
- Uses UDP packet switching with ACK replies with the server. This is to maintain speed and minimize network usage.

clientFileTransferThread: Dedicated thread for P2P file transfer
- Uses TCP file transfer protocol between clients.
- This allows multiple clientFileTransferThreads to transfer files to users in parallel

clientHandshakeThread: Single thread in charge of spawning clientFileTransferThreads:
- This parent thread allows the Napster user to receive requests in the background for filesharing without having the entire application stop midprogress

clientUtils: Basic client-side functions and data for socket programming

fileTransfer: Serializable object that functions as primary source of communication between server and client
- FileTransfer objects have specific protocols under 'Typesetter' that dictate server or client action (see docs)

serverDatabase: Server that processes client requests
- Processes client requests and loads the request information into Hashmaps
- Each active client has a dedicated thread spawned that is in charge of communicating with that client
- Hashmap is utilized to hold client packet information. Threads read Hashmap for their respective clients and processes the information in parallel of each other.

serverThread: Threads used by serverDatabase to communicate with each client

serverUtils: Basic server-side functions and data for socket programming

FILES WORK IN PROGRESS:

clientStatistics: Object accessed only by the server that holds user statistics in application
- Used to designate ratings based on user statistics. Ratings determine the most 'dependable' clients for p2p file transfers.
- Statistics are read and maintained by the server to ensure fair ratings between users

fileTree: Data structure that uses TreeMaps to ensure the best user ratings for a given file is chosen

serverPingThread: Heartbeat thread that communicates with client to ensure their network is still online
