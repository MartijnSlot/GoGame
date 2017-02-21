package com.nedap.university.go.server;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.gocommands.DetermineCommand;

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
    private int colorInt;

    /**
     * threaded clienthandler constructor
     *
     * @param socket socket
     * @param server GoServer
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
                Command command = DetermineCommand.determineServerCommand(inputFromClient.readLine(), this);
                command.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;

    }

    /**
     * getter for the color integer
     * 0 = black
     * 1 = white
     * @return
     */
    int getColorInt() {
        return colorInt;
    }


    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    String getClientName() {
        return clientName;
    }

    int getDim() {
        return dim;
    }

    /**
     * kicks a player from the server for making an illegal move
     *
     * @throws IOException
     */
    public void annihilatePlayer() throws IOException {
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
     * @param input string
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
     * @param name string
     * @return boolean
     */
    private boolean checkName(String name) {
        return !(name.length() > 20 | name.matches(".*\\W+.*"));
    }

    /**
     * checks whether the given dimension is parsable and correct
     *
     * @param input string
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
     * @param message string
     */
    public void writeToClient(String message) {
        try {
            outputToClient.write(message);
            outputToClient.newLine();
            outputToClient.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    /**
     * setter for singlegameserver
     *
     * @param singleGameServer singlegameserver
     */
    void setSingleGameServer(SingleGameServer singleGameServer) {
        this.singleGameServer = singleGameServer;
    }

    /**
     * make sure the name of this clientHandler is set
     * enters this clienthandler into the list of clients on the server
     *
     * @param splitMessage)  which is a split String list of the entry by the player
     */
    public void enterPlayerName(String[] splitMessage) {
        if (splitMessage.length == 2 && checkName(splitMessage[1]) && clientName == null) {
            clientName = splitMessage[1];
            server.clientSet.add(this);
            writeToClient("CHAT server - Great success! You have entered your name: " + clientName);
        } else {
            writeToClient("WARNING Please enter PLAYER followed by a lowercase name. " + clientName +
                    ", name requirements: \n- name < 20 characters \n- name may only consist out of digits and letters. " +
                    "\n Or you already have entered a name.");
        }
    }

    public void cancelWaiting(String[] splitMessage) {
        if (splitMessage.length == 1) {
            this.setClientStatus(ClientStatus.PREGAME);
            server.statusWaitingToInitial(this);
            writeToClient("CHAT server - Great success! You have set your status to PREGAME. Please enter GO boardsize, " + clientName);
        } else {
            writeToClient("WARNING Status not changed, you are still waiting for a game. ");
        }
    }

    public void chatToAll(String[] splitMessage) {
        String message = sowString(splitMessage);
        server.chatToAllPlayers(message);
    }

    public void chatToOpponent(String[] splitMessage) {
        String message = sowString(splitMessage);
        singleGameServer.chatToGamePlayers(message);
    }

    private String sowString(String[] splitMessage) {
        String temp = null;
        for (String messageItem : splitMessage) {
            temp = temp + messageItem + " ";
        }
        temp = temp != null ? temp.trim() : null;
        return temp;
    }

    public void enterDimension(String[] splitMessage) {
        if (splitMessage.length == 2 && checkDim(splitMessage[1])) {

            writeToClient("CHAT server - Great success! You have set your status to PREGAME. Please enter GO boardsize, " + clientName);
        } else {
            writeToClient("WARNING Status not changed, you are still waiting for a game. ");
        }
    }

    public void turnTableflip() {
        singleGameServer.executeTurnTableflip(this);
    }
}