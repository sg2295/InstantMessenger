package Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * General Controller class, implements methods used by Controller classes
 * and defines the main Controller method, implements Runnable.
 * ServerController and BotController extend it.
 */
public abstract class Controller implements Runnable {

    /**
     * Used to get user input
     */
    private BufferedReader input;

    /**
     * Used to indicate if this Controller thread is currently running or not
     */
    private boolean running;

    /**
     * Prepares Controller to read input form the console and sets the running flag to true.
     */
    public Controller() {
        this.input = new BufferedReader(new InputStreamReader(System.in)); // Intialise BufferedReader to get input from console
        this.running = true;
    }

    /**
     * Getter method for the BufferedReader used to get user input
     * @return BufferedReader used for getting user input
     */
    public BufferedReader getInput() {
        return input;
    }

    /**
     * Setter method for the BufferedReader
     * @param input The BufferedReader to set the Controller's BufferedReader to
     */
    public void setInput(BufferedReader input) {
        this.input = input;
    }

    /**
     * Checks if this Controller is currently running
     * @return true if the Controller is currently running, false if it has been terminated
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Terminates this Controller, by setting the running flag to false.
     */
    public void terminate() {
        this.running = false;
    }

    /**
     * Shuts down this Controller by closing the BufferedReader and setting the
     * running flag to false.
     */
    public void shutdown() {
        try {
            getInput().close(); // Closes the BufferedReader used to get input

            if (this.isRunning()) { // Checks that the controller is still running
                this.terminate(); // Sets running flag to false
            }
        } catch (IOException e) {
            System.err.println("Error closing the input stream.");
        }
    }

    /**
     * Creates and starts a new Controller Thread
     * @Override
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Defines the main Controller method to be implemented by Controller subclasses.
     * @Override
     */
    public abstract void run();
}
