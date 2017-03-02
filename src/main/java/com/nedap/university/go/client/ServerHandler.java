package com.nedap.university.go.client;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.gocommands.*;
import com.nedap.university.go.model.Position;
import com.nedap.university.go.viewer.GoGUIIntegrator;
import javafx.application.Platform;

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
    private Game game;
    private Socket socket;
    private GoGUIIntegrator gogui;
    private String color;
    private ClientStatus clientStatus = ClientStatus.PREGAME;


    public ServerHandler(GoClient client, Socket socket) {
        this.client = client;
        this.socket = socket;
        this.gogui = new GoGUIIntegrator(false, true, 1);
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
            while (socket != null && socket.isConnected()) {
                String fromServer = inputFromServer.readLine();
                if (fromServer != null) {
                    DetermineCommand determineCommand = new DetermineCommand();
                    Command command = determineCommand.determineClientCommand(fromServer, this);
                    command.execute();
                }

            }
            client.shutdown();
        } catch (IOException e) {
            System.out.println("No input");
        }
    }

    private void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }


    String getClientName() {
        return clientName;
    }

    public Game getGame() {
        return game;
    }

    Socket getSocket() {
        return socket;
    }

    void setClientName(String clientName) {
        this.clientName = clientName;
    }

    private void writeToServer(String message) {
        try {
            outputToServer.write(message);
            outputToServer.newLine();
            outputToServer.flush();
        } catch (IOException e) {
            System.out.println("You played on a crappy server. Server has died, disconnection just happened.");
            shutdown();
        }
    }

    private void shutdown() {
        try {
            outputToServer.close();
            inputFromServer.close();
            Platform.exit();
            client.shutdown();
        } catch (IOException e) {
            System.out.println("Trying to shutdown. Everything is GOne. ");
        }
    }

    private String sewString(String[] splitMessage) {
        return String.join(Protocol.DELIMITER, splitMessage);
    }


    private void switchTurns() {
        if (clientStatus == ClientStatus.INGAME_NOT_TURN) {
            setClientStatus(ClientStatus.INGAME_TURN);
            synchronized (client) {
                client.notify();
            }
            System.out.println("Your turn, " + color + " " + clientName);
        } else {
            setClientStatus(ClientStatus.INGAME_NOT_TURN);
            System.out.println("NOT your turn, " + color + " " + clientName);
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
        game = new Game(boardSize);
        gogui.setBoardSize(boardSize);
        gogui.startGUI();
        clientStatus = (color.equals("white") ? ClientStatus.INGAME_NOT_TURN : ClientStatus.INGAME_TURN);
        synchronized (client) {
            client.notify();
        }
        System.out.println("New game started on a board with dimension " + splitMessage[3] + " \nYour stone: " + splitMessage[1] + "\nYour opponent: " + splitMessage[2]);
        if (color.equals("black")) {
            System.out.println("\n\nYou can start, young padawan.");
        }
    }

    /**
     * checks the input of 'VALID' given by the server and acts on it locally
     *
     */
    public void handleValid(String[] splitMessage) {
        boolean white = (splitMessage[1].equals("white"));
        int x = Integer.parseInt(splitMessage[2]);
        int y = Integer.parseInt(splitMessage[3]);
        game.doMove(x, y);
        gogui.addStone(x, y, white);
        for (Position a : game.autoRemoveSet) {
            gogui.removeStone(a.getX(), a.getY());
        }
        game.autoRemoveSet.clear();
        switchTurns();
    }

    public void handleInvalid(String[] splitMessage) {
        System.out.println("Game has ended due to INVALID move of player " + splitMessage[1] + "\n" + splitMessage[2]);
        if (splitMessage[1].equals(color)) {
            shutdown();
        } else {
            clearGameSetPregame();
        }
    }

    public void handlePassed(String[] splitMessage) {
        game.passMove();
        System.out.println("Player " + splitMessage[1] + " has passed.");
        if (!clientStatus.equals(ClientStatus.PREGAME)) {
            switchTurns();
        }
    }

    public void handleEnd(String[] splitMessage) {
        int scoreBlack = Integer.parseInt(splitMessage[1]);
        int scoreWhite = Integer.parseInt(splitMessage[2]);
        System.out.println("Game has ended. Score black: " + scoreBlack + "\nScore white: " + scoreWhite);
        clearGameSetPregame();
    }

    public void handleIncomingChat(String[] splitMessage) {
        String chat = sewString(splitMessage);
        System.out.println(chat);
    }

    public void handleTableFlipped(String[] splitMessage) {
        String chat = sewString(splitMessage);
        System.out.println(chat);
        clearGameSetPregame();
    }

    public void handleWarning(String[] splitMessage) {
        String chat = sewString(splitMessage);
        System.out.println(chat);
    }

    void checkAndSendPlayerMove(int x, int y) {
        if (game != null && game.moveAllowed(x, y)) {
            writeToServer("MOVE " + x + " " + y);
        } else {
            System.out.println("Illegal move, do not send this move to the server. Try again.");
        }
    }

    void sendPlayerCommand(String[] splitMessage) {
        String commandToSend = sewString(splitMessage);
        writeToServer(commandToSend);
    }

    private void clearGameSetPregame() {
        setClientStatus(ClientStatus.PREGAME);
        System.out.println("ClientStatus: " + ClientStatus.PREGAME + " Please enter your GO dim.");
        try {
            inputFromServer = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        game.reset();
        gogui.clearBoard();
    }



}
