package com.nedap.university.go.client;

import java.io.*;
import java.net.*;

/**
 * Class for creating a GO client.
 *
 * @author Martijn Slot
 * @version 1.0
 */

public class GoClient extends Thread {


    private Socket socket;
    private BufferedReader inputFromPlayer;
    private ServerHandler serverHandler;


    public GoClient(String serverAddress, int serverPort) throws IOException {
        System.out.println("Client connecting to port " + serverPort + "\n Server IP: " + serverAddress);
        try {
            socket = new Socket(serverAddress, serverPort);
            inputFromPlayer = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        serverHandler = new ServerHandler(this, socket);
        serverHandler.start();
        try {
            this.handleInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getter for the socket
     *
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * handles the input from the server.
     *
     * @throws IOException
     */
    public void handleInput() throws IOException {
        System.out.println("Please enter PLAYER + 'yourname'");
        do {
            String message = inputFromPlayer.readLine();
            if (message != null) {
                String inputMessage[] = message.split(" ");
                if (message.startsWith("PLAYER") && inputMessage.length == 2 && checkName(inputMessage[1])) {
                    serverHandler.initName(inputMessage[0], inputMessage[1]);
                    break;
                } else if (message.startsWith("GO") && inputMessage.length == 2 && checkDim(inputMessage[1]) && serverHandler.getClientName() != null) {
                    serverHandler.initGame(inputMessage[0], inputMessage[1]);
                    break;
                } else if (message.startsWith("MOVE") && isParsable(inputMessage[1]) && isParsable(inputMessage[2]) && serverHandler.getClientName() != null) {
                    serverHandler.move(inputMessage[0], inputMessage[1], inputMessage[2]);
                    break;
                } else if (message.startsWith("PASS") && inputMessage.length == 1 && serverHandler.getClientName() != null) {
                    serverHandler.pass(inputMessage[0]);
                    break;
                } else if (message.startsWith("TABLEFLIP") && inputMessage.length == 1 && serverHandler.getClientName() != null) {
                    serverHandler.tableflip(message);
                    break;
                } else if (message.startsWith("CHAT")) {
                    serverHandler.chat(message);
                    break;
                } else if (message.startsWith("EXIT") && inputMessage.length == 1) {
                    serverHandler.writeToServer(message);
                    serverHandler.shutdown();
                    this.shutdown();
                    break;
                } else if (message.startsWith("CANCEL") && inputMessage.length == 1) {
                    serverHandler.shutdown();
                    break;
                } else if (message.isEmpty()) {
                    System.out.println("WARNING " + message + " is invalid input.");
                    break;
                }
            }
        } while (true);
    }

    /**
     * checks whether a string input can be parsed to Integer
     *
     * @param input
     * @return boolean
     */
    public boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * checks whether the inputname is correct
     *
     * @param name
     * @return boolean
     */
    public boolean checkName(String name) {
        if (name.length() > 20 | name.matches(".*\\W+.*")) {
            System.out.println("Illegal input " + name +
                    ", name requirements: \n- name < 20 characters \n- name may only consist out of digits and letters");
            return false;
        }
        System.out.println("Your name is: " + name);
        return true;
    }

    /**
     * checks whether the given dimension is parsable and correct
     *
     * @param input
     * @return boolean
     */
    public boolean checkDim(String input) {
        int parsedInput;
        if (!isParsable(input)) {
            return false;
        } else {
            parsedInput = Integer.parseInt(input);
            if (parsedInput % 2 == 0 || parsedInput < 5 || parsedInput > 131) {
                return false;
            }
        }
        System.out.println("Board size OK: " + input);
        return true;
    }

    /**
     * shuts down the client.
     *
     * @throws IOException
     */
    public void shutdown() throws IOException {
        inputFromPlayer.close();
        socket.close();
        System.exit(0);
    }

}

