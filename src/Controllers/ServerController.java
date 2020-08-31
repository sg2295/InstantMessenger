package Controllers;

import Server.ChatServer;

import java.io.IOException;

/**
 * Used to control the ChatServer; listens to user input for commands and takes
 * action accordingly.
 * Extends the Controller Class.
 */
public class ServerController extends Controller {

    /**
     * Server controlled by this ServerController
     */
    private ChatServer server;

    /**
     * Constructor that initialises a ServerController
     * @param server Server to be controlled by this ServerController
     */
    public ServerController(ChatServer server) {
        super(); // Calls constructor of parent class
        this.server = server;
    }

    /**
     * Listens to user input for the "EXIT" command and shuts down the ChatServer
     * if the command is detected.
     * @Override
     */
    public void run() {
        String message;
        try {
            while (this.isRunning()) { // Checks that the application has not been terminated
                message = getInput().readLine(); // Gets user input using a BufferedReader
                if (message.equals("EXIT")) {
                    this.server.shutdown(); // Shuts down the ChatServer
                    this.terminate(); // Sets running flag to false to exit the loop
                } else {
                    System.out.println("> Unknown command.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting line from input stream.");
        } finally {
            this.shutdown(); // Shuts down the ServerController
        }
    }
}
