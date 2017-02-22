package com.nedap.university.go.client;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.gocommands.*;
import com.nedap.university.go.viewer.GoGUIIntegrator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Class for creating a Server.
 * handles all communication between client and ClientHandler
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class ServerHandler extends Thread {

    private final GoClient client;
    private BufferedReader inputFromServer;
    private BufferedWriter outputToServer;
    private String clientName;
    private Integer dim;
    private Game game;
    private Socket socket;
    private GoGUIIntegrator gogui;
    private String color;
    private ClientStatus clientStatus;


    public ServerHandler(GoClient client, Socket socket) {
        this.client = client;
        this.socket = socket;

        try {
            inputFromServer = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            outputToServer = new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
        } catch (IOException ioe) {
            ioe.getStackTrace();

        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String fromServer = inputFromServer.readLine();
                Command command = DetermineCommand.determineClientCommand(fromServer, this);
                command.execute();

            }
            client.shutdown();
        } catch (IOException e1) {
            System.out.println("No input");
        }
    }

    void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    ClientStatus getClientStatus() {
        return clientStatus;
    }

    String getClientName() {
        return clientName;
    }

    void writeToServer(String message) throws IOException {
        outputToServer.write(message);
        outputToServer.newLine();
        outputToServer.flush();

    }

    private void shutdown() {
        try {
            outputToServer.close();
            inputFromServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sewString(String[] splitMessage) {
        String temp = null;
        for (String messageItem : splitMessage) {
            temp = temp + messageItem + Protocol.DELIMITER;
        }
        temp = temp != null ? temp.trim() : null;
        return temp;
    }


    private void switchTurns() {
        if (clientStatus == ClientStatus.INGAME_NOT_TURN) {
            setClientStatus(ClientStatus.INGAME_TURN);
            System.out.println("CHAT server - your turn, " + color + clientName);
        } else {
            setClientStatus(ClientStatus.INGAME_NOT_TURN);
            System.out.println("CHAT server - NOT your turn, " + color + clientName);
        }
    }

    /**
     * checks what to do when the initial splitMessage is given by the GoClient
     *
     * @param splitMessage that contains the READY command
     */
    public void handleReady(String[] splitMessage) {
        int boardSize = Integer.parseInt(splitMessage[3]);
        color = splitMessage[1];
        dim = boardSize;
        game = new Game(boardSize);
        clientStatus = (color.equals("white") ? ClientStatus.INGAME_NOT_TURN : ClientStatus.INGAME_TURN);
        System.out.println("New game started on a board with dimension " + splitMessage[4] + " \nYour stone:" + splitMessage[1] + "\nYour opponent:" + splitMessage[2]);
    }

    /**
     * checks the input of 'VALID' given by the server and acts on it locally
     *
     */
    public void handleValid(String[] splitMessage) {
// boolean white = (splitMessage[2] == "white");
        int x = Integer.parseInt(splitMessage[2]);
        int y = Integer.parseInt(splitMessage[3]);
        game.doMove(x, y);
        switchTurns();
    }

    public void handleInvalid(String[] splitMessage) {
        System.out.println("Game has ended due to INVALID move of player " + splitMessage[1] + "\n" + splitMessage[2]);
        shutdown();
        client.shutdown();
    }

    public void handlePassed(String[] splitMessage) {
        game.passMove();
        System.out.println("Player " + splitMessage[1] + " has passed.");
        switchTurns();
    }

    public void handleEnd(String[] splitMessage) {
        int scoreBlack = Integer.parseInt(splitMessage[1]);
        int scoreWhite = Integer.parseInt(splitMessage[2]);
        System.out.println("Game has ended. Score black: " + scoreBlack + "\nScore white: " + scoreWhite);
        shutdown();
        client.shutdown();
    }

    public void handleIncomingChat(String[] splitMessage) {
        String chat = sewString(splitMessage);
        System.out.println(chat);
    }

    public void handleTableFlipped(String[] splitMessage) {
        String chat = sewString(splitMessage);
        System.out.println(chat);
    }

    public void handleWarning(String[] splitMessage) {
        String chat = sewString(splitMessage);
        System.out.println(chat);
    }
}
