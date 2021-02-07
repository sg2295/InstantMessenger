# Instant Messenger 💬
## About the application 📄
This Instant Messaging application  was written as an extension to a university assigned coursework, which was used to assess students' understanding of multithreading and networking in Java.
> The application consists of a Server, Client, and Bot component.
### Chat Server 🖥️
The multi-threaded Server spawns threads to handle incoming Client requests in parallel, in order to supports multiple client connections simultaneously. When the Server receives a message from a client, it broadcasts it to all connected clients. In addition, the Server does not stop if one or more clients disconnect from it. It can shut down cleanly by the user entering the "EXIT" command on the terminal. Instructions on how to run the Server can be found below.
### Chat Client 💻
The Client, once connected, is capable of sending, and receiving messages from the Server. The Client supports reading input form the console and displaying all messages received by the server at the same time, by using a multi-threaded solution. The Client can directly interact with the Server using some predefined commands ("NAME", "EXIT"). Instructions on how to run the Client can be found below.
### Chat Bot 🤖
The Bot functions like any other Client. Once started, the Bot connects to the Server and begins interacting with other Clients, who use the '@bot' identifier. Whenever the Bot receives a message including the identifier, it will generate a suitable response, document it in the Bot's console, and send it to the Server. The Bot can disconnect from the server, and cleanly shut down by typing the "EXIT" command in the command line.

## Instructions ⚙️
### Server
  - To start the Server, run the ChatServer Class.
  - You can use the -csp optional parameter to change the port that is used to listen for new Client connections.
    Example: java ChatServer -csp 14005. The default port is 14001.
  - To cleanly shut down the Server the user can enter the "EXIT" command, which will inform all connected Clients, the
    Server is shutting down.

### Client
  - The Client can be started by running the ChatClient Class.
  - You can use the -cca optional parameter to change the IP address the Client attempts to connect to.
    Example: java ChatClient -cca 192.168.10.250. The default address is localhost.
  -You can use the -ccp optional parameter to change the port the Client attempts to connect to.
    Example: java ChatClient -ccp 14005. The default port is 14001.
  - You can pass use the -cca and -ccp optional parameters together, in order to change the IP address and port.
    Example: java ChatClient -cca 192.168.10.250 -ccp 14005.
  - To disconnect from the Server, and cleanly shut down the Client, the user can enter "EXIT".

### Bot
  - The Bot can be started by running the ChatBot Class.
  - The Bot functions like any other Client, thus it supports the same optional parameters (-ccp and -cca).
  - Once the bot is connected to the Server, the bot will generate and send suitable responses to Client messages that
    include the '@bot' identifier.
  - Any response sent by the Bot is documented and printed on the console, for the user to see.
  - To disconnect from the Server, and cleanly shut down the Bot, the user can enter "EXIT".
