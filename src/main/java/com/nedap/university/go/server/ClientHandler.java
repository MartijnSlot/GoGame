package com.nedap.university.go.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Class for creating a ClientHandler.
 * handles all communication between server and ServerHandler
 *
 * @author Martijn Slot
 * @version 1.0
 */

public class ClientHandler extends Thread {
    private GoServer server;
    private SingleGameServer singleGameServer;
    private Socket socket;
    private BufferedWriter outputToClient;
    private BufferedReader inputFromClient;
    private ClientStatus clientStatus;
    private int dim;
    private String clientName;

    /**
     * threaded clienthandler constructor
     *
     * @param socket
     * @param server
     */
    public ClientHandler(Socket socket, GoServer server) {
        this.server = server;
        this.socket = socket;
        this.clientStatus = ClientStatus.PREGAME;
        try {
            inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * pre game run-function for clientStatuses PREGAME and WAITING
     */
    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                switch (clientStatus) {
                    case PREGAME:
                        preGameInput();
                        writeToClient("CHAT server: ClientStatus " + clientStatus);
                        break;
                    case WAITING:
                        waitingInput();
                        writeToClient("CHAT server: ClientStatus " + clientStatus);
                        break;
                    case INGAME:
                        gameInput();
                        writeToClient("CHAT server: ClientStatus " + clientStatus);
                        break;
                    default:
                        System.out.println("Hier mag ik nooit komen...");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setClientStatus(ClientStatus cs) {
        this.clientStatus = cs;

    }

    ClientStatus getClientStatus() {
        return clientStatus;
    }

    String getClientName() {
        return clientName;
    }

    int getDim() {
        return dim;
    }

    /**
     * sends out the READY message with playercolor, opponentname and boardsize.
     *
     * @param color
     * @param opponent
     * @param boardSize
     * @throws IOException
     */
    void sendReady(String color, String opponent, int boardSize) throws IOException {
        outputToClient.write("READY " + color + " " + opponent + " " + boardSize);
        outputToClient.newLine();
        outputToClient.flush();
    }

    /**
     * Possible inputs from the client with the status PREGAME and the following actions
     *
     * @throws IOException
     */
    private void preGameInput() throws IOException {

    }


    /**
     * Possible inputs from the client with the status WAITING and the following actions
     *
     * @throws IOException
     */
    private void waitingInput() throws IOException {

    }

    /**
     * Possible inputs from the client with the status INGAME and the following actions
     *
     * @throws IOException
     */
    public void gameInput() throws IOException {

    }

    /**
     * kicks a player from the server for making an illegal move
     *
     * @throws IOException
     */
    public void annihilatePlayer() throws IOException {
        singleGameServer.otherPlayerWins();
        writeToClient("CHAT You've been caught cheating, therefore you shall be annihilated!");
        server.pendingClients.get(this.getDim()).remove(this);
        server.clientSet.remove(this);
        server.clientHandlerMap.remove(this);
        outputToClient.close();
        inputFromClient.close();
        server.socket.close();
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
            writeToClient("CHAT Server: Illegal input " + name +
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
        boolean dimIsOk = true;
        int parsedInput;
        if (!isParsable(input)) {
            dimIsOk = false;
        } else {
            parsedInput = Integer.parseInt(input);
            if (parsedInput % 2 == 0 || parsedInput < 5 || parsedInput > 131) {
                dimIsOk = false;
            }
        }
        return dimIsOk;
    }

    /**
     * asks the client to play again
     *
     * @return boolean
     */
    private boolean playAgain() {
        return true;
    }

    /**
     * general message writer from server to client
     *
     * @param message
     * @throws IOException
     */
    void writeToClient(String message) {
        try {
            outputToClient.write(message);
            outputToClient.newLine();
            outputToClient.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    /**
     * @param singleGameServer
     */
    public void setSingleGameServer(SingleGameServer singleGameServer) {
        this.singleGameServer = singleGameServer;
    }

    /**
     * enum for ClientStatus
     *
     * @author martijn.slot
     */
    public enum ClientStatus {

        PREGAME, WAITING, INGAME;
    }

}