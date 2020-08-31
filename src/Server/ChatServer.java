package Server;

import Controllers.ServerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * Starts listening and prepares for Client connections.
 * Establishes a two-way connection with each Client connected to the Server,
 * and handles each Client accordingly.
 */
public class ChatServer {
    /**
     * ServerSocket used for accepting new client connections
     */
    private ServerSocket serverSocket;
    /**
     * The port number the ServerSocket is listening to
     */
    private int port;

    /**
     * Used to indicate whether or not the Server is running
     */
    private boolean running;

    /**
     * Default constructor, creates a ServerSocket listening to the default port.
     * Sets port number to 14001.
     */
    public ChatServer() {
        this(14001);
    }

    /**
     * Creates a ServerSocket listening to the specified port.
     * @param port
     */
    public ChatServer(int port) {
        this.port = port;
        try {
            System.out.println("> Starting Server with Port number: " + this.port + ".");
            this.serverSocket = new ServerSocket(this.port); // Instantiate ServerSocket to listen for Client connections
            this.running = true;
            System.out.println("> Server is listening for connections.");
            System.out.println("> To shut down the server type \"EXIT\".");
        } catch (IOException | IllegalArgumentException e) { // Catch Exceptions related to initializing the Server
            System.err.println("Error initializing Server with the given port number: " + this.port +".");
            System.out.println("> Server has shut down.");
        }
    }

    /**
     * Getter method for the ServerSocket used for listening to new connections.
     * @return The ServerSocket used to listen for connecting clients
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Setter method for the ServerSocket used for listening to new connections.
     * @param serverSocket New ServerSocket to be used
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Getter method for the Port number used for listening to new connections.
     * @return The Port number used for listening to connecting clients
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter method for the Port number used for listening to new connections.
     * @param port New Port number to be used for listening to connecting clients
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Checks if the Server is currently running (listening for connections).
     * @return true if it is listening for connections, otherwise false
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Sets the running flag to false, indicating the Server has been shut down.
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Shuts down the Server by closing the ServerSocket and informs connected
     * clients the Server is shutting down.
     */
    public void shutdown() {
        if (this.isRunning()) { // Checks that the Server is running
            try {
                System.out.println("> Server is shutting down.");

                // Send message informing of clients that the Server shut down:
                ClientHandler.informAll("The Server has shut down, enter \"EXIT\" to disconnect.");

                this.serverSocket.close(); // Closes the Socket used for listening to new connections
                this.terminate(); // Sets running flag to false
            } catch (IOException e) {}
        }

    }

    /**
     * Starts threads responsible for handling new client connections and for
     * controlling the Server.
     */
    private void start() {
        if (!this.isRunning()) return; // Checks that the Server has started successfully

        // Start thread for managing user input
        ServerController controller = new ServerController(this);
        controller.start();

        try {
            while (controller.isRunning()) { // Checks if "EXIT" command has been issued by the controller
                // Create new ClientHandler thread to manage new client connection
                ClientHandler newClient = new ClientHandler(this.serverSocket.accept());
                newClient.start(); // Start thread to manage the connection
            }
        } catch (SocketException s) {
            // ServerSocket has been closed, move to the finally segment
        } catch (IOException e) {
            System.err.println("Error establishing new connection.");
        } finally {
            ClientHandler.removeAll(); // Kick off all currently connected clients and terminate their threads
            this.shutdown(); // Shuts down the Server
        }
    }

    public static void main(String[] args) {
        int port = 14001;
        String portStr = null;

        // Search and extract the -csp parameter
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-csp")) {
                portStr = args[i + 1];
            }
        }

        // Try to convert the given port number from String to int
        if (portStr != null) { // Check that a port number has been given
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.err.println("Error with the given port argument.\nConnecting to default port.");
            }
        }

        new ChatServer(port).start(); // Start a new ChatServer with the given parameters
    }
}
