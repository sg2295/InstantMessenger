package Controllers;

import Clients.ChatBot;

import java.io.IOException;

/**
 * Used to control the ChatBot; listens to user input for commands and takes 
 * action accordingly.
 * Extends the Controller Class.
 */
public class BotController extends Controller {

    /**
     * ChatBot that is being controlled by this BotController
     */
    private ChatBot bot;

    /**
     * Constructor that initialises a BotController
     * @param bot ChatBot to be controlled by this BotController
     */
    public BotController(ChatBot bot) {
        super(); // Calls constructor of parent class
        this.bot = bot;
    }

    /**
     * Getter method for the ChatBot being controlled.
     * @return The ChatBot that is being controlled
     */
    public ChatBot getBot() {
        return bot;
    }

    /**
     * Setter method for the ChatBot being controlled.
     * @param bot The ChatBot to be controlled
     */
    public void setBot(ChatBot bot) {
        this.bot = bot;
    }

    /**
     * Processes the user's message, by checking for commands and responding
     * accordingly.
     * List of commands:
     * 1. EXIT = Disconnects from the server and shuts down the bot
     * @param command User input to be processed
     */
    private void processCommand(String command) {
        if (command.equals("EXIT")) { // Checks for "EXIT" command
            this.bot.shutdown(); // Shuts down the ChatBot
            this.terminate(); // Sets running flag to false
        } else {
            System.out.println("> Unknown command.");
        }
    }

    /**
     * Listens to user input, checks for the "EXIT" command and shuts down the 
     * ChatBot if the command is detected.
     * @Override
     */
    public void run() {
        String message;
        try {
            while (this.isRunning()) { // Checks that the application has not been terminated
                message = getInput().readLine(); // Gets user input using a BufferedReader
                this.processCommand(message);
            }
        } catch (IOException e) {
            System.err.println("Error getting line from input stream.");
        } finally {
            this.shutdown(); // Shuts down the BotController
        }
    }
}