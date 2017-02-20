package com.nedap.university.go.client;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.model.Stone;
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
    private boolean white;


    public ServerHandler(GoClient client, Socket socket) {
        this.client = client;
        this.socket = socket;

        try {
            inputFromServer = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            outputToServer = new BufferedWriter(new OutputStreamWriter(client.getSocket().getOutputStream()));
        } catch (IOException ioe) {

        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String fromServer = inputFromServer.readLine();
                handleGame(fromServer);
            }
        } catch (IOException e1) {
            System.out.println("No input");
        }
    }

    String getClientName() {
        return clientName;
    }

    /**
     * Handles all incoming messages from the clientHandler
     *
     * @param fromServer message from server
     */
    private void handleGame(String fromServer) {

        while (fromServer != null) {
            String serverInputMessage[] = fromServer.split(" ");
            if (fromServer.startsWith("WAITING")) {
                System.out.println(fromServer);
                break;
            } else if (fromServer.startsWith("READY")) {
                String color = serverInputMessage[1];
                if (serverInputMessage[1] == "white") {
                    white = true;
                } else {
                    white = false;
                }
                String opponent = serverInputMessage[2];
                dim = Integer.parseInt(serverInputMessage[3]);
                System.out.println("Your name: " + clientName + "\nYour color: " + color + "\nYour opponent: " + opponent);
				game = new Game(dim);
                break;

            } else if (fromServer.startsWith("VALID")) {
                int col = Integer.parseInt(serverInputMessage[2]);
                int row = Integer.parseInt(serverInputMessage[3]);
                game.executeTurn(row, col);
                addToGUI(row, col);
                System.out.println("Move " + fromServer);
                break;
            } else if (fromServer.startsWith("INVALID") && serverInputMessage.length == 1) {
                System.out.println(fromServer + ": kicked from the server due to invalid move");
                try {
                    client.shutdown();
                } catch (IOException e) {
                    System.out.println("client cannot be shutdowned.");
                    e.printStackTrace();
                }
                break;
            } else if (fromServer.startsWith("PASSED")) {
                game.passMove();
                System.out.println("Other player " + fromServer);
                break;
            } else if (fromServer.startsWith("WARNING")) {
                System.out.println(fromServer + " try something else");
                break;
            } else if (fromServer.startsWith("TABLEFLIPPED")) {
                System.out.println(fromServer);
                game.tableflipMove();
                break;
            } else if (fromServer.startsWith("CHAT")) {
                System.out.println(fromServer);
                break;
            } else if (fromServer.startsWith("END")) {
                int scoreBlack = Integer.parseInt(serverInputMessage[1]);
                int scoreWhite = Integer.parseInt(serverInputMessage[2]);
                System.out.println("Score black: " + scoreBlack + "\nScore white: " + scoreWhite);
            } else if (!fromServer.isEmpty()){
                System.out.println("WARNING in the input from server. " + fromServer + " is invalid input.");
                break;
            }
        }
    }

    void writeToServer(String message) throws IOException {
        outputToServer.write(message);
        outputToServer.newLine();
        outputToServer.flush();

    }

    void shutdown() throws IOException {
        outputToServer.close();
        inputFromServer.close();
    }

    /**
     * checks whether a move is allowed and not in KO
     *
     * @param col
     * @param row
     * @return boolean
     */
    private boolean moveAllowed(int col, int row) {
        if (!game.getBoard().isAllowed(col, row)) {
            System.out.println("\nField " + col + ", " + row + " is no valid position.");
            return false;
        } else if (game.inKo(col, row)) {
            System.out.println("\nField " + col + ", " + row + " is in Ko. \n\nMaar wie is die Ko dan?");
            return false;
        } else {
            return true;
        }
    }

    /**
     * checks what to do when the initial command is given by the GoClient
     *
     * @param player
     * @param name
     * @throws IOException
     */
    void initName(String player, String name) throws IOException {
        clientName = name;
        writeToServer(player + " " + name);
    }

    /**
     * checks what to do when the initial command is given by the GoClient
     *
     * @param go
     * @param boardSize
     * @throws IOException
     */
    void initGame(String go, String boardSize) throws IOException {
        dim = Integer.parseInt(boardSize);
        gogui = new GoGUIIntegrator(false, true, dim);
        gogui.startGUI();
        gogui.setBoardSize(dim);
        writeToServer(go + " " + boardSize);
    }

    /**
     * checks the input of 'MOVE' given by the GoClient and passes it on to the server
     *
     * @param stringX
     * @param stringY
     * @param move
     * @throws IOException
     */
    void move(String move, String stringX, String stringY) throws IOException {
        int x = Integer.parseInt(stringX);
        int y = Integer.parseInt(stringY);
        if (!moveAllowed(x, y)) {
            System.out.print(move + " " + stringX + " " + stringY + " incorrect, try again");
        } else {
            writeToServer(move + " " + stringX + " " + stringY);
        }
    }

    void handleCancel(String message) {
        try {
            writeToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gogui.stopGUI();

    }

    /**
     * adds a stone to the GUI, only the game is allowed to do this.
     *
     * @param x
     * @param y
     */
    public void addToGUI(int x, int y) {
        gogui.addStone(x, y, white);
    }

    /**
     * removes a stone from the GUI, only the game is allowed to do this.
     *
     * @param x
     * @param y
     */
    private void removeFromGUI(int x, int y) {
        gogui.removeStone(x, y);
    }



}
