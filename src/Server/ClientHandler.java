package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles a single connection between the Server and a Client.
 * Implements Runnable.
 * Used on the Server side.
 */
public class ClientHandler implements Runnable {

    /**
     * Socket used to establish connection between the Client and Server
     */
    private Socket clientSocket;
    /**
     * A unique username used to identify each client
     */
    private String clientName;
    /**
     * Used to get messages from the client
     */
    private BufferedReader input;
    /**
     * Used to send server responses to the client
     */
    private PrintWriter output;

    /**
     * ArrayList containing all current client connection handlers
     */
    private static volatile ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    /**
     * Keeps track of the number of total connections established; used
     * to assign a unique number to each new connecting client
     */
    private static volatile int clientNumber = 0;

    /**
     * A Reentrant lock used to access synchronised segments of code
     */
    private static Lock lock = new ReentrantLock();

    /**
     * Creates new ClientHandler object to handle a new client connection
     * through the specified Socket.
     * Assigns the client a unique username, initialises the required input and
     * output data streams and appends the current client list
     * @param clientSocket
     */
    public ClientHandler(Socket clientSocket) {
        this.clientName = "[Client " + clientNumber + "]"; // Assign unique username
        this.clientSocket = clientSocket;
        try {
            // Initialise input and output data streams used to communicate between the client and server
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.output = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()), true);
            ClientHandler.newConnection(this); // Update the current list of connected clients
        } catch (IOException e) {
            System.out.println("Problem connecting client: " + this.clientName);
        }
    }

    /**
     * Getter method for the Socket used for the connection.
     * @return The Socket that is used for the connection
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Setter method for the Socket used for the connection.
     * @param clientSocket The new Socket to be used for the connection
     */
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Getter method for the Client's name.
     * @return The Client's name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Setter method for the Client's name.
     * @param clientName The new name to be assigned to the Client
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Updates the current list of connected clients and sends a list of
     * commands to the new client.
     * Uses a Reentrant lock to ensure synchronisation.
     * Prints a message informing of addition of the client.
     * @param newClient The new client to be added to the list
     */
    private static void newConnection(ClientHandler newClient) {
        try {
            lock.lock(); // Used to synchronise the updating of the list
            clients.add(newClient); // Adds new client to the list
            clientNumber++; // Adjust the clientNumber for the next connection
        } catch (Exception e) {
            System.err.println("Error adding client to list.");
        } finally {
            System.out.println("> Client: " + newClient.clientName + " has been added to the list.");
            lock.unlock(); // Unlocks the synchronised block of code
            sendCommands(newClient); // Sends a list of commands on how to navigate the server
            informAll("New connection. Client: " + newClient.clientName + " has connected." ); // Inform connected clients of new connection
        }
    }

    /**
     * Removes a client from the list of connected clients, closing any input
     * and output streams associated with them as well as the Socket they used.
     * Uses a Reentrant lock to ensure synchronisation.
     * @param client Client to be removed from the list
     */
    public static void removeClient(ClientHandler client) {
        if (client.clientSocket.isClosed()) { // Check if the client has already been removed
            return;
        }

        try {
            lock.lock(); // Used to synchronise the list updating
            broadcast(client, "[Server]: You've been disconnected from the server."); // Inform the Client they are being removed

            clients.remove(client); // Update the list of clients

            client.terminate(); // Close the input, output streams and Sockets used
        } catch (Exception e) {
            System.err.println("Error removing client from list.");
        } finally {
            informAll("Client " + client.clientName + " has left the Server."); // Inform all connected clients that a client left
            System.out.println("> Client: " + client.clientName + " has been removed from the list.");
            lock.unlock(); // Unlock the synchronised block of code
        }
    }

    /**
     * Closes the Socket used for the connection, and the input and output data
     * streams used.
     */
    private void terminate() {
        try {
            this.clientSocket.close(); // Close the Socket associated with this Client
            // Close the IO stream readers/writers used:
            this.input.close();
            this.output.close();
        } catch (SocketException s) {
            System.err.println("Error closing " + this.clientName + "'s client socket.");
        } catch (IOException e) {
            System.err.println("Error closing input/output stream/writer");
        }
    }

    /**
     * Removes all connected clients from the server.
     */
    public static void removeAll() {
        if (clients.isEmpty()) { // If there are no current connections, return
            return;
        }
        try {
            lock.lock(); // Synchronise the accessing of the list of connections
            while (!clients.isEmpty()) {
                removeClient(clients.remove(0)); // Remove each connection
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Broadcasts a message from to a specified client.
     * @param client The recipient of the message
     * @param message The message to be sent to the client
     */
    private static void broadcast(ClientHandler client, String message) {
        client.output.println(message); // Send the message using the PrintWriter
    }

    /**
     * Broadcasts a message to all connected clients.
     * @param message The message to be sent to all clients
     */
    private void broadcastToAll(String message) {
        try {
            lock.lock(); // Synchronise the looping through the list of clients
            message = this.clientName + ": " + message; // Add the username of the Client sending the message
            for (ClientHandler client : clients) { // Iterate through the list of clients
                broadcast(client, message); // broadcast the message to each client
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Broadcasts a message from the Server to all clients.
     * @param message The message from the server to be broadcasted
     */
    public static void informAll(String message) {
        try {
            lock.lock(); // Synchronise the looping through the list of clients
            message = "[Server]: " + message;
            for (ClientHandler client : clients) { // Iterate through the list of clients
                broadcast(client, message); // broadcast the message to each client
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sends a list of commands to a client, when they first connect.
     * List of commands:
     * 1. NAME "name" = set name for client
     * 2. QUIT = exit server
     * @param client Client that just connected
     */
    private static void sendCommands(ClientHandler client) {
        // Array of commands:
        String commandArray[] = {"[Server]: To change your name type: \"NAME\" followed by a space and your desired name.",
                "[Server]: To disconnect and exit the application, please send message: \"EXIT\"."};
        // Iterate through the array of commands and send each command seperately
        for (int i = 0; i < commandArray.length; i++) {
            broadcast(client, commandArray[i]); // Send every command to the client
        }
    }

    /**
     * Checks whether a name is unique or is already in use by another client.
     * @param clientName The name to be assigned
     * @return true if the name is unique and false if it has already been used by another client
     */
    private boolean checkUniqueName(String clientName) {
        boolean nameTaken = true; // Used to indicate if the name is unique
        try {
            lock.lock(); // Synchronise the searching of the list of clients

            for (ClientHandler client : clients) {
                if (client.clientName.equalsIgnoreCase("[" + clientName + "]")) {
                    nameTaken = false; // Name is already in use by another client
                }
            }

        } finally {
            lock.unlock(); // Release the synchronised block of code
        }
        return nameTaken;
    }

    /**
     * Changes the name of a client to a username of their choice.
     * @param message The message containing the name change command
     */
    private void changeName(String message) {
        if (message.length() < 5) { // Name command received is in the wrong format
            broadcast(this, "[Server]: Error setting name, type \"NAME\" followed by a space and your desired name."); // Send error message
        } else {

            String requestedName = message.substring(5); // Extract the desired name

            if (!this.checkUniqueName(requestedName) || requestedName.equalsIgnoreCase("Server")) { // Check if name is taken or not allowed
                broadcast(this, "[Server]: Error setting name, the name you requested is already in use by another client or is not allowed.");
            } else {
                // Inform all clients of the name change and set the clients username to the new name:
                informAll("Client " + this.clientName + " has changed their name to [" + requestedName + "].");
                this.clientName = "[" + requestedName + "]"; // Change client's name
            }
        }
    }

    /**
     * Creates and starts a new ClientHandler thread.
     * @Override
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Gets client input, checks for commands and broadcasts it to all other clients.
     * If a command is detected the corresponding function is called.
     * Finally checks if the client has been disconnected.
     * @Override
     */
    public void run() {
        try {
            String message;
            while (true) {
                message = input.readLine(); // Gets input from the Client

                if (message == null) {
                    break;
                }
                // Prints out message stating that a message has been received
                System.out.println("Received: \"" + message + "\" from client " + this.clientName);

                // Checks for commands
                if (message.startsWith("NAME")) {
                    this.changeName(message); // Calls function to handle name change
                } else {
                    broadcastToAll(message); // Broadcasts message to all other clients
                }
            }
        } catch (SocketException s) {
        } catch (IOException e) {
            System.err.println("Error getting input line from client: " + this.clientName);
        } finally {
            boolean connectedFlag = true; // Flag to indicate if the client is still connected

            // Check if the Client is still connected
            try {
                lock.lock(); // Synchronise the accessing of the list of clients
                connectedFlag = clients.contains(this); // Update the flag
            } finally {
                lock.unlock();
            }

            if (connectedFlag) { // If the client is still connected, remove them
                removeClient(this); // Remove client
            }
        }
    }
}
