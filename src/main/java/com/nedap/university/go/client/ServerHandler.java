package com.nedap.university.go.client;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.gocommands.DetermineCommand;
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
    private ClientStatus clientStatus;


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
                DetermineCommand.determineClientCommand(fromServer, this);
            }
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

    void shutdown() throws IOException {
        outputToServer.close();
        inputFromServer.close();
    }

    /**
     * checks what to do when the initial splitMessage is given by the GoClient
     *
     * @param player
     * @param name
     * @throws IOException
     */
    void initName(String player, String name) throws IOException {
    }

    /**
     * checks what to do when the initial splitMessage is given by the GoClient
     *
     * @param go
     * @param boardSize
     * @throws IOException
     */
    void initGame(String go, String boardSize) throws IOException {

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

    }


    void handleCancel(String message) {
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
