Client
-Handles sending information to the server.
Client Receive Thread
-Handles printing / dealing with messages sent to client. If result is global or private message, just display.
--If server message, handle accordingly

Server
-Opens a thread for each connected user to allow for concurrency.
-Opens a thread to handle inputs to server console (for special server commands).
Server Handle Client Thread
-Thread that opens a Socket with each client and communicates with it.
Server Commands Thread
-Thread that checks console for user input and acts according to the user input.
-Can shut down or show users from here.


Protocol
-Information between server and client is shared through JSON arrays.
-4 types of messages: Global message, Private message, User connect message, and server message.
-Global message:
--Simple global chat room message containing of sender name and text.
-Private message:
--Simple private message containing sender, recipient, and text.
--Server checks to make sure the nickname of the sender and the nickname listed as the sender match
--Client checks to make sure nickname of recipient and nickname listed as recipient match
--Then prints message.
--Checks for correct user happen on both client and server. Users have a known list of users and can't send messages
--outside of it, and server will tell sender if recipient no longer exists.