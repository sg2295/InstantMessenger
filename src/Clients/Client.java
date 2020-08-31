package Clients;

import java.io.IOException;
import java.net.Socket;

/**
 * General Client Class, establishes connection with the Server.
 * The ChatClient and ChatBot classes extend Client.
 */
public abstract class Client {
    /**
     * Defines the port used to establish connection to the Server
     */
    private int port;
    /**
     * Defines the Address used to establish connection to the Server
     */
    private String address;
    /**
     * Socket used to establish connection to the Server
     */
    private Socket socket;

    /**
     * Flag used to indicate whether a client has successfully connected to the server or not
     * true if the connection was successful, false otherwise
     */
    private boolean connected;

    /**
     * Default constructor, uses Port number 14001 and address "localhost".
     */
    public Client() {
        this(14001, "localhost");
    }

    /**
     * Uses the provided port and address to establish a connection to the Server.
     * @param port Port number to use for the connection
     * @param address Address to be used for the connection
     */
    public Client(int port, String address) {
        this.connected = false;
        this.port = port;
        this.address = address;
        try {
            System.out.println("> Starting Client with port number: " + this.port +" and address: " + this.address);
            this.socket = new Socket(this.address, this.port); // Instantiate Socket to establish connection
            System.out.println("> Client has connected.");
            this.connected = true; // Sets the connected flag to true
        } catch (IOException e) {
            System.out.println("> Failed to connect to Server with specified port number and address.");
        }
    }

    /**
     * Getter method for the port number used for the connection.
     * @return The port number used for the connection as an integer
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Getter method for the address used for the connection.
     * @return The address of the Server as a String
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Getter method for the Socket used for the connection
     * @return Socket used for the connection
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Setter method for the port number.
     * @param port Port number to connect to
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Setter method for the Servers' address.
     * @param address Address of the Server to connect to
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Setter method for the Socket
     * @param socket Socket to be used for the connection
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Checks if the Client is currently connected to the Server.
     * @return true if the client is connected, false if it's not
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sets the connected flag to false, indicating the Client has disconnected
     * from the Server.
     */
    public void disconnect() {
        this.connected = false;
    }

    /**
     * Defines the main Client method to be implemented by Client subclasses.
     * @Override
     */
    public abstract void start();
}
