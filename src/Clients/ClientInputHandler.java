package Clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Listens for user input and sends the message to the Server.
 * Implements Runnable.
 * Used on the Client side.
 * @author sgavr
 */
public class ClientInputHandler implements Runnable {
    /**
     * Socket used to establish the connection between the client and server
     */
    private Socket socket;
    /**
     * BufferedReader used to get user input from the command line
     */
    private BufferedReader input;
    /**
     * PrintWriter used to send the user's message to the server
     */
    private PrintWriter output;
    /**
     * Used to indicate if this ClientInputHandler thread is currently running or not
     */
    private boolean running;

    /**
     * Creates a new ClientInputHandler Object using the provided Socket.
     * Instantiates input and output data streams used to exchange information
     * between the client and the server, and set the running flag to true.
     * @param socket Socket used to establish a connection with the Server
     */
    public ClientInputHandler(Socket socket) {
        try {
            this.socket = socket;
            // Initialise the IO stream readers/writers:
            this.input = new BufferedReader(new InputStreamReader(System.in)); // Used to get user input from command line
            this.output = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true); // Used to send messages to the Server
            this.running = true;
        } catch (SocketException s) { // If an error occurs, prints out an error message and shuts down the application:
            System.err.println("Error with passed socket.");
            this.shutdown();
        } catch (IOException e) {
            System.err.println("Error creating input handler.");
            this.shutdown();
        } catch (NullPointerException n) {
            System.err.println("Unable to connect to server.Shutting down.");
            this.shutdown();
        }
    }

    /**
     * Checks if this ClientInputHandler is currently running.
     *
     * @return true if the ClientHandler is currently running, false if it has
     * been terminated
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Terminates this ClientInputHanlder, by setting the running flag to false.
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Shuts down this ClientInputHandler.
     * By closing the Socket used to establish the connection with the Server,
     * the input and output data streams responsible for communicating with the
     * server and sets the running flag to false.
     */
    public void shutdown() {
        try {
            this.socket.close(); // Closes the Socket used to establish a connection with the Server
            // Closes the IO streams used:
            this.input.close();
            this.output.close();

            if (this.isRunning()) { // Terminate the thread if it has not been terminated already
                this.terminate();
            }
        } catch (SocketException s) {
            System.err.println("Error closing socket.");
        } catch (IOException e) {
            System.err.println("Error closing input or output stream readers/writers.");
        } catch (NullPointerException n) {
            System.err.println("Error connecting to server.");
        }
    }

    /**
     * Creates and starts a new ClientHandler Thread
     * @Override
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Gets user input from the console, checks for the "EXIT" command, and
     * broadcasts it to the Server.
     * Finally it shuts down the ClientInputHandler thread.
     * @Override
     */
    public void run() {
        try {
            String message;
            while (this.isRunning()) { // Repeat while this thread has not been terminated
                message = input.readLine(); // Get user input from the console

                if (message == null || message.equals("EXIT")) { // Check for error condition and EXIT command
                    this.terminate(); // Used to indicate this thread has been terminated
                    break;
                }

                if (!message.equals("")) {
                    output.println(message); // Sends user message to Server
                }
            }
        } catch (SocketException s) {
        } catch (IOException e) {
        } finally {
            this.shutdown(); // Shuts down the ClientInputHandler
        }
    }
}
