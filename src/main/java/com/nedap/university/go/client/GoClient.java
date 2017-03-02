package com.nedap.university.go.client;

import com.nedap.university.go.aiStrategies.AiBassie;
import com.nedap.university.go.aiStrategies.AiKasparov;
import com.nedap.university.go.aiStrategies.Strategy;
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
    private boolean ai = false;
    private String inputFromComputerPlayer;
    private Strategy aiPlayer;


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
        while (serverHandler.getSocket().isConnected() && serverHandler.getSocket() != null) {
            if (inputFromPlayer != null) {
                runHuman();
            }
            if (ai) {
                runAI();

            }
        }
    }

    Socket getSocket() {
        return socket;
    }

    /**
     * whenever an AI is involved, this method is run, it makes a distinction between client stati.
     *
     */
    private void runAI() {
        switch (serverHandler.getClientStatus()) {
            case INGAME_TURN:
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inputFromComputerPlayer = aiPlayer.determineMove(serverHandler.getGame());
                DetermineCommand determineCommand = new DetermineCommand();
                Command command = determineCommand.inputCommand(inputFromComputerPlayer, this);
                command.execute();
                break;
            case INGAME_NOT_TURN:
            case WAITING:
            case PREGAME:
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    /**
     * whenever an Human player is involved, this method is run.
     *
     */
    private void runHuman() {
        String fromPlayer;
        try {
            fromPlayer = inputFromPlayer.readLine();
            DetermineCommand determineCommand = new DetermineCommand();
            Command command = determineCommand.inputCommand(fromPlayer, this);
            command.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMoveFromPlayer(int x, int y) {
        serverHandler.checkAndSendPlayerMove(x, y);
    }

    public void handleAnythingFromPlayerExceptMoveExitGoChatAndPlayer(String[] splitMessage) {
        serverHandler.sendPlayerCommand(splitMessage);
    }


    public void handleChatCommandFromPlayer(String[] splitMessage) {
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

    public void handleGoFromPlayer(String[] splitMessage) {
        if (serverHandler.getClientName() != null) {
            serverHandler.sendPlayerCommand(splitMessage);
            switch (serverHandler.getClientName()) {
                case "clownbassie":
                    System.out.println("Asjemenou, Clown Bassie komt je pakken! ");
                    ai = true;
                    aiPlayer = new AiBassie();
                    inputFromPlayer = null;
                    break;
                case "garrykasparov":
                    System.out.println("Kasparov: goeie schaker, slechte GO-er. ");
                    ai = true;
                    aiPlayer = new AiKasparov();
                    inputFromComputerPlayer = aiPlayer.determineMove(serverHandler.getGame());
                    inputFromPlayer = null;
                    break;
                default:
                    break;
            }
        } else {
            System.out.println("You have no name, enter PLAYER name first. ");
        }
    }

    /**
     * shuts down the client.
     *
     */
    void shutdown() {
        try {
            System.out.println("Exited from server. Disconnection just happened.");
            inputFromPlayer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Some things were already closed, but we close it anyway. For your convenience.");
        }
        System.exit(0);
    }

}

