package com.nedap.university.go.client;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.gocommands.DetermineCommand;

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
                String fromPlayer = inputFromPlayer.readLine();
                Command command = DetermineCommand.inputCommand(fromPlayer, this);
                command.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Socket getSocket() {
        return socket;
    }

    public void handleMoveFromPlayer(int x, int y) {
        serverHandler.checkAndSendPlayerMove(x, y);
    }

    public void handleAnythingFromPlayerExceptMove(String[] splitMessage) {
        serverHandler.sendPlayerCommand(splitMessage);
    }

    public void handleExitFromPlayer(String[] splitMessage) {
        serverHandler.sendPlayerCommand(splitMessage);
        shutdown();
    }

    public void handlePlayerCommandFromPlayer(String[] splitMessage) {
        serverHandler.sendPlayerCommand(splitMessage);
        serverHandler.setClientName(splitMessage[1]);
    }


    /**
     * shuts down the client.
     *
     */
    void shutdown() {
        try {
            inputFromPlayer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}

