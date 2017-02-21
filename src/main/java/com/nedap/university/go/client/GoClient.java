package com.nedap.university.go.client;

import com.nedap.university.go.model.Stone;
import com.nedap.university.go.viewer.GoGUIIntegrator;

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
        while(inputFromPlayer != null) {
            try {
                this.handleInput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Socket getSocket() {
        return socket;
    }

    /**
     * handles the input from the server.
     *
     * @throws IOException
     */
    private void handleInput() throws IOException {

    }

    /**
     * checks whether a string input can be parsed to Integer
     *
     * @param input
     * @return boolean
     */
    private boolean isParsable(String input) {
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
    private boolean checkName(String name) {
        if (name.length() > 20 | name.matches(".*\\W+.*")) {
            System.out.println("Illegal input " + name +
                    ", name requirements: \n- name < 20 characters \n- name may only consist out of digits and letters");
            return false;
        }
        return true;
    }

    /**
     * checks whether the given dimension is parsable and correct
     *
     * @param input
     * @return boolean
     */
    private boolean checkDim(String input) {
        int parsedInput;
        if (!isParsable(input)) {
            return false;
        } else {
            parsedInput = Integer.parseInt(input);
            if (parsedInput % 2 == 0 || parsedInput < 5 || parsedInput > 131) {
                return false;
            }
        }
        return true;
    }

    /**
     * shuts down the client.
     *
     * @throws IOException
     */
    void shutdown() throws IOException {
        inputFromPlayer.close();
        socket.close();
        System.exit(0);
    }

}

