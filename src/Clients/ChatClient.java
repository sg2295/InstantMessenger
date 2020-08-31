package Clients;

/**
 * Establishes connection between the Client and the Server.
 * Provides the interface for communication between Server and Client.
 * Extends the Client class.
 */
public class ChatClient extends Client{

    /**
     * Default constructor, establishes a connection using the default port and address values.
     * Creates Socket using address "localhost" and port number 14001.
     */
    public ChatClient() {
        super(); // Calls default constructor of the parent class
    }

    /**
     * Establishes a connection using the provided port and address values.
     * @param port Port number to be used for the connection
     * @param address Address to be used for the connection
     */
    public ChatClient(int port, String address) {
        super(port, address); // Calls constructor of the parent class
    }

    /**
     * Starts threads to handle user input and server responses, monitors if any
     * of the two threads have been shut down and shuts down the other in response.
     * Checks if any of the client's threads have been terminated every 500ms.
     * @Override
     */
    public void start() {
        // Checks if the Client has successfully connected to the Server.
        if (!this.isConnected()) {
            System.out.println("> Error connecting Client to given port and address.");
            return;
        }
        // Thread for handling user input
        ClientInputHandler inputHandler = new ClientInputHandler(this.getSocket());
        inputHandler.start();
        // Thread for handling server response
        ServerHandler serverHandler = new ServerHandler(this.getSocket());
        serverHandler.start();

        try {
            // Check every 500ms if any of the two threads have shut down:
            while (serverHandler.isRunning() && inputHandler.isRunning()) {
                Thread.sleep(500);
            }
        } catch (InterruptedException i) {
            System.err.println("Error caused by sleeping thread.");
        } finally {
            // Checks which thread has not been shut down and shuts it down forcefully
            if (serverHandler.isRunning()) serverHandler.shutdown();
            if (inputHandler.isRunning()) inputHandler.shutdown();

            this.disconnect(); // Sets the connected flag to false
        }
    }

    public static void main(String[] args) {
        String address = "localhost";
        int port = 14001;
        String portStr = null;

        // Search and extract -cca and -ccp parameters
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-cca")) {
                address = args[i + 1];
            } else if (args[i].equals("-ccp")) {
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

        new ChatClient(port, address).start(); // Start a new ChatClient with the given parameters
    }
}
