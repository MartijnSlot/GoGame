package com.nedap.university.go.server;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.gocommands.DetermineCommand;
import com.nedap.university.go.gocommands.Protocol;

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
    private String color;

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
                String fromClient = inputFromClient.readLine();
                Command command = DetermineCommand.determineServerCommand(fromClient, this);
                command.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;

    }

    String getColor() {
        return color;
    }

    void setColor(String color) {
        this.color = color;
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public String getClientName() {
        return clientName;
    }

    int getDim() {
        return dim;
    }

    /**
     * kicks a player from the server for making an illegal move
     *
     * @throws IOException for socketclose
     */
    void annihilatePlayer()  {
        try {
            outputToClient.close();
            inputFromClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.eraseClient(this);
    }

    /**
     * sews a splitted string back together
     * @param splitMessage list of strings
     * @return string
     */
    private String sewString(String[] splitMessage) {
        return String.join(Protocol.DELIMITER, splitMessage);
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
    public void handlePlayerCommand(String[] splitMessage) {
        clientName = splitMessage[1];
        server.clientSet.add(this);
        writeToClient("CHAT server - Great success! You have entered your name: " + clientName);
    }

    public void handleCancelCommand() {
        this.setClientStatus(ClientStatus.PREGAME);
        server.statusWaitingToInitial(this);
        writeToClient("CHAT server - Great success! You have set your status to PREGAME. Please enter GO boardsize, " + clientName);
    }

    public void chatToAll(String[] splitMessage) {
        String message = sewString(splitMessage);
        server.chatToAllPlayers(message);
    }

    public void chatToOpponent(String[] splitMessage) {
        String message = sewString(splitMessage);
        singleGameServer.sendToPlayers(message);
    }


    public void handleGoCommand(String[] splitMessage) {
        dim = Integer.parseInt(splitMessage[1]);
        server.addToWaitingList(this, dim);
        writeToClient("CHAT server - Great success! You have set your status to WAITING. Please wait for another player, " + clientName);
    }

    public void handleTableflipCommand() {
        singleGameServer.executeTurnTableflip(this);
    }

    public void handleExitCommand(String[] splitMessage) {
        server.eraseClient(this);
    }

    public void handleMoveCommand(String[] splitMessage) {
        int x = Integer.parseInt(splitMessage[1]);
        int y = Integer.parseInt(splitMessage[2]);
        singleGameServer.executeTurnMove(x, y, this);
        writeToClient("CHAT server - Great success! You have set your moved a stone to " + x + "," + y + ". \nTurn finished, so don't try anything funny " + clientName);
    }

    public void handlePassCommand(String[] splitMessage) {
            singleGameServer.executeTurnPass(this);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientHandler that = (ClientHandler) o;

        if (singleGameServer != null ? !singleGameServer.equals(that.singleGameServer) : that.singleGameServer != null)
            return false;
        return color != null ? color.equals(that.color) : that.color == null;
    }

    @Override
    public int hashCode() {
        int result = singleGameServer != null ? singleGameServer.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }



}