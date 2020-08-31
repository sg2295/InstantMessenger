package Clients;

import Controllers.BotController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Random;

/**
 * Connects to the server as a client, reads each message sent by the server,
 * generates an appropriate response and sends it back to the server.
 * Extends the Client Class.
 */
public class ChatBot extends Client {

    /**
     * Used to send messages to the server
     */
    private PrintWriter output;
    /**
     * Used to receive messages from the server
     */
    private BufferedReader input;

    /**
     * Holds preset responses to client messages
     */
    private HashMap<String, String> responses;

    /**
     * Default constructor, establishes a connection to port 14001 and address localhost.
     */
    public ChatBot() {
        this(14001, "localhost");
    }

    /**
     * Establishes connection, prepares for communication with the Server, sets
     * the Bot's name, initialises and populates the HashMap.
     *
     * @param port Port number to connect to
     * @param address Address to connect to
     */
    public ChatBot(int port, String address) {
        super(port, address); // Calls the constructor of the parent class
        if (this.isConnected()) { // Check that the Bot has successfully connected to the Server
            try {
                // Initialise input/output streams to handle communcation with server:
                this.input = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));
                this.output = new PrintWriter(new OutputStreamWriter(this.getSocket().getOutputStream()), true);

                this.setName(); // Sets the name of the bot
                this.responses = new HashMap<String, String>(); // Initialises HashMap
                this.populateHashMap(); // Calls function to populate HashMap with preset responses to client messages
                System.out.println("> Bot Initialized.");
            } catch (IOException e) {
                System.out.println("> Failed to Initialize Bot.");
            }
        }
    }

    /**
     * Populates HashMap with preset responses corresponding to client messages.
     */
    private void populateHashMap() {
        // The keyes are client messages, each key corresponds to a prescripted message
        this.responses.put("hello", "Hi there!"); // Adds new entry in the HashMap
        this.responses.put("hi", "Hey!");
        this.responses.put("hey", "Hello!");
        this.responses.put("bye", "See you soon!");
        this.responses.put("goodbye", "Bye!");
        this.responses.put("thanks", "My pleasure!");
        this.responses.put("okay", "Sounds good.");
        this.responses.put("yes", "Great to hear!");
        this.responses.put("awesome", "That's more like it!");
        this.responses.put("great", "Cool!");
        this.responses.put("okay", "Great!");
        this.responses.put("good", "Cool.");
        this.responses.put("yeah", "Nice.");
        this.responses.put("how", "Who am I to say?");
        this.responses.put("yup", "Coolio.");
        this.responses.put("okay", "Alright!");
        this.responses.put("ok", "Sounds good!");
        this.responses.put("no", "Why is that?");
        this.responses.put("news", "Not that I know of.");
        this.responses.put("haha", "Good one, right?");
        this.responses.put("new", "Nope, nothing new.");
    }

    /**
     * Closes the Client Socket (if it is still open) and the Input/Output Streams,
     * and finally outputs on the console that the bot has been terminated.
     */
    public void shutdown() {
        if (!this.getSocket().isClosed()) {
            try {
                this.getSocket().close(); // Closes the Socket used to communicate with the Server
                // Closes the BufferedReader and PrintWriter
                this.input.close();
                this.output.close();
                this.disconnect(); // Set connected flag to false
                System.out.println("> The bot has been disconnected from the server.\n> Enter \"EXIT\" to terminate the program.");
            } catch (SocketException s) {
                System.err.println("Error closing socket.");
            } catch (IOException e) {
                System.err.println("Error closing IO streams.");
            } catch (NullPointerException n) {
                System.err.println("Error shutting down the bot.");
            }
        }
    }

    /**
     * Picks and sets the name for the bot.
     * Sends a message to the server requesting that the name of the bot be
     * changed to the selected name
     */
    private void setName() {
        String names[] = {"Chad", "Timothy", "Marcus", "Dominic", "Elliot", "Duffy"};
        String name = names[new Random().nextInt(names.length)]; // Picks a random name from the array of names
        this.output.println("NAME BOT " + name); // Sends message to server requesting name be set to selected name
    }

    /**
     * Checks the received message for exceptions.
     * Used to detect the Server getting shutdown.
     * @param message
     */
    private void messageExceptions(String message) {
        if (message.equals("[Server]: You've been disconnected from the server.")) {
            this.shutdown(); // If the server has shut down, shut down the bot
        }
    }


    /**
     * Checks if the HashMap contains a response for any word in the client's message
     * and returns the corresponding response.
     * @param messageArray Client message broken up into an array
     * @return String response or null if HashMap does not contain the given key
     */
    private String lookUpResponses(String messageArray[]) {
        // Iterate through the array of Strings
        for (String word: messageArray) {
            // If a word is a key in the HashMap, return the corresponding String
            if (this.responses.containsKey(word)) return responses.get(word);
        }
        return null;
    }

    /**
     * Called when a client message has not got any preset response, returns a random response.
     * The generated response does not depend on the client's message.
     * Uses a Random object to select a random String from an array of responses
     * @return A random response
     */
    private String randomResponse() {
        // Array of random responses:
        String randomResponses[] = { "Just like mother always said, sometimes some people deserve a good high five, in the face, with a chair.",
                "You've probably never had Sunday Roast have you?", "Oh well.", "Mitochondria is the powerhouse of the cell.",
                "Well the dinosaurs probably said something along those lines when they saw the meteor heading their way.",
                "What? Sorry, I wasn't paying attention", "\"Call me maybe\" Is the best song ever written. Don't @ me."};
        // Uses the Random.nextInt() function to generate a random integer corresponding to an index in the response array
        return randomResponses[new Random().nextInt(randomResponses.length)]; // Returns a randomly selected String from the array
    }

    /**
     * Called when a client message contains a question mark and it cannot be handled by the HashMap,
     * returns a random response the the question.
     * The generated response does not depend on the client's message.
     * Uses a Random object to select a random String from an array of responses
     * @return A randomly generated response to a question
     */
    private String questionResponse() {
        // Array of question responses:
        String questionResponses[] = {"I see.", "Go on.", "What do you think?",
                "Ah, I've almost got it.", "I don't know.", "No clue.", "I don't know.",
                "Think about it, one more time."};
        // Uses the Random.nextInt() function to generate a random integer corresponding to an index in the response array
        return questionResponses[new Random().nextInt(questionResponses.length)]; // Returns a randomly selected String from the array
    }

    /**
     * Calls the appropriate functions to generate a response to the client's message.
     * Called if the client's message cannot be handled by the HashMap, i.e. there is
     * no coded response for the message.
     * @param message String received by the Bot
     * @return A response to the client's message
     */
    private String responseExceptions(String message) {
        if (message.endsWith("?")) return this.questionResponse(); // Call function to handle question
        return this.randomResponse(); // Call function to generate a random response
    }


    /**
     * Processes the client's message and returns the corresponding response to be sent to the server.
     * Calls the appropriate functions to generate a response.
     * Checks for abnormal events (Shutting down of the Server) and handles them accordingly.
     * @param message String received by the bot (Sent over the Server)
     * @return A String response to the received message
     */
    private String processMessage(String message) {
        String response = null;
        // Check to see if message was sent by a client, the bot itself or the server
        if (message.startsWith("[BOT ") || !message.startsWith("[") || message.startsWith("[Server]:")) {
            this.messageExceptions(message); // Handle any message from the server accordingly
        } else {
            message = message.toLowerCase(); // Converts the String to lowercase to be handled

            if (!message.contains("@bot")) { // Check if message is direct at the bot
                return response; // If it is not directed at the bot, ignore it
            }

            message = message.substring(message.indexOf(":") + 2); // Remove the name of the client
            String messageWords [] = message.split("[\\p{Punct}\\s]+"); // Break the message down into an array of Strings

            response = this.lookUpResponses(messageWords); // Attempt to get response using the HashMap

            if (response == null) {
                response = responseExceptions(message); // Call method to handle unexpected messages
            }
        }
        return response;
    }

    /**
     * Starts thread to control the bot, listens for messages sent by clients,
     * calls functions to process the messages and sends response to the Server.
     * @Override
     */
    public void start() {
        if (!this.isConnected()) {
            System.out.println("> Error connecting Bot to given port and address.");
            return;
        }
        // Start thread to listen to the user's input
        BotController botController = new BotController(this);
        botController.start();
        String response, message;
        try {
            while (botController.isRunning()) { // Checks if the user input tried to terminate the bot
                message = input.readLine(); // Gets message from server

                if (message == null) {
                    break;
                }

                response = this.processMessage(message); // calls function to generate response

                if (response != null) {
                    System.out.println("\t> Sending message: " + response); // document message
                    output.println(response); // sends response to the server
                }
            }
        } catch (SocketException s) {
        } catch (IOException e) {
        } finally {
            this.shutdown(); // shuts down the bot
        }

    }

    public static void main(String[] args) {
        String address = "localhost";
        int port = 14001;
        String portStr = null;

        // Search and extract -cca and -ccp parameters
        for (int i =0; i < args.length - 1; i++) {
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

        new ChatBot(port, address).start(); // Start a new ChatBot with the given parameters
    }
}
