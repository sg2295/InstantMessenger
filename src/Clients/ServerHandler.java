package Clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Receives and prints out the messages sent out by the Server.
 * Implements the Runnable Interface.
 * It is used on the Client side.
 */
public class ServerHandler implements Runnable {

    /**
     * Socket used to establish the connection between the client and server
     */
    private Socket socket;
    /**
     * BufferedReader used to get server responses
     */
    private BufferedReader input;

    /**
     * Used to indicate if this ServerHandler thread is currently running or not
     */
    private boolean running;

    /**
     * Creates a new ServerHandler Object using the provided Socket.
     * Instantiates the BufferedReader used to get Server responses, and sets the
     * running flag to true (indicating the ServerHandler thread is active).
     * @param socket Socket used to establish a connection with the Server
     */
    public ServerHandler(Socket socket) {
        try {
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); // Instanciates BufferedReader used to get Server Responses
            this.running = true;
        } catch (SocketException s) { // If an error occurs, prints out an error message and shuts down the application:
            System.err.println("Error with passed socket.");
            this.shutdown();
        } catch (IOException e) {
            System.err.println("Error creating input stream from socket.");
            this.shutdown();
        } catch (NullPointerException n) {
            System.err.println("Error connecting to server. Shutting down.");
            this.shutdown();
        }
    }

    /**
     * Checks if this ServerHandler is currently running.
     *
     * @return true if the ServerHandler is currently running, false if it has
     * been terminated
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Terminates this ServerHandler, by setting the running flag to false.
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Shuts down the ServerHandler, closing the Socket and the BufferedReader used.
     * Closes the Socket used to establish the connection and the BufferedReader
     * used to get Server responses.
     * Sets the running flag to false.
     */
    public void shutdown() {
        try {
            this.socket.close(); // Closes the Socket used for the connection
            this.input.close(); // Closes the BufferedReader
            if (this.isRunning()) {
                this.terminate(); // If it has not been done already, set the running flag to false
            }
        } catch (SocketException s) {
            System.err.println("Error closing socket.");
        } catch (IOException e) {
            System.err.println("Error closing input stream readers");
        } catch (NullPointerException n) {
            System.err.println("Error connecting to server.");
        }
    }

    /**
     * Creates and starts a new ServerHandler Thread.
     * @Override
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Gets messages sent by the Server, prints them and finally calls the
     * appropriate method to shutdown this ServerHandler thread.
     * Finally it shuts down the ServerHandler thread.
     * @Override
     */
    public void run() {
        try {
            String serverMessage; // Holds the message sent by the Server

            while (this.isRunning()) { // Repeat while this thread has not been terminated
                serverMessage = this.input.readLine(); // Get message from the Server

                if (serverMessage == null) {
                    this.terminate(); // Used to turn the running flag to false, indicating that the thread has been terminated
                    break;
                }
                System.out.println(serverMessage); // Print out the message received from the Server
            }
        } catch (SocketException s) {
        } catch (IOException e) {
        } finally {
            this.shutdown(); // Shuts down the ServerHandler
        }
    }
}
