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
    private SingleGameServer sgs;
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
                        writeToClient("CHAT ClientStatus: " + clientStatus);
                    case WAITING:
                        waitingInput();
                        writeToClient("CHAT ClientStatus: " + clientStatus);
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * setter for clientStatus
     *
     * @param cs
     */
    public void setClientStatus(ClientStatus cs) {
        this.clientStatus = cs;

    }

    /**
     * getter for clientStatus
     *
     * @return ClientStatus
     */
    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    /**
     * getter for clientName
     *
     * @return String
     */
    private String getClientName() {
        return clientName;
    }

    /**
     * getter for the board dimension
     *
     * @return int
     */
    private int getDim() {
        return dim;
    }

    /**
     * setter for the board dimension
     *
     * @param
     */
    public void setDim(int d) {
        dim = d;
    }

    /**
     * sends out the READY message with the pre-determined parameters.
     *
     * @param color
     * @param opponent
     * @param boardSize
     * @throws IOException
     */
    public synchronized void sendReady(String color, String opponent, int boardSize) throws IOException {
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

        String message = inputFromClient.readLine();
        while (message != null && clientStatus == ClientStatus.PREGAME) {
            String inputMessage[] = message.split(" ");
            if (message.startsWith("PLAYER") && inputMessage.length == 2 && checkName(inputMessage[1])) {
                ;
                clientName = inputMessage[1];
                writeToClient("Your name is: " + clientName);
                System.out.println("Name entered: " + clientName);
                break;
            } else if (message.startsWith("GO") && inputMessage.length == 2 && checkDim(inputMessage[1])) {
                setDim(Integer.parseInt(inputMessage[1]));
                server.clientEntry(this, dim);
                writeToClient("Dimension entered: " + dim);
                System.out.println("Dimension entered: " + dim);
                break;
            } else if (message.startsWith("CHAT")) {
                server.chatToAllPlayers(clientName + message);
                break;
            } else if (message.startsWith("EXIT") && inputMessage.length == 1) {
                try {
                    this.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    outputToClient.close();
                    inputFromClient.close();
                    server.removeClient(this);
                }
            } else {
                outputToClient.write("WARNING PreGameInput: Must...resist...kicking...you." + message + " is invalid input. "
                        + "Please enter PLAYER name*, GO dim, CHAT message or EXIT: " + clientStatus);
                outputToClient.newLine();
                outputToClient.flush();
                break;
            }
        }
    }

    /**
     * Possible inputs from the client with the status WAITING and the following actions
     *
     * @throws IOException
     */
    private void waitingInput() throws IOException {
        String message = inputFromClient.readLine();

        while (message != null && clientStatus == ClientStatus.WAITING) {
            String inputMessage[] = message.split(" ");
            if (message.startsWith("CHAT")) {
                System.out.println(message);
                break;
//				server.chatToAllPlayers(clientName + message);
            } else if (message.startsWith("EXIT") && inputMessage.length == 1) {
                try {
                    this.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputToClient.close();
                inputFromClient.close();
                server.removeClient(this);
            } else {
                outputToClient.write("WARNING waitingInput: Must...resist...kicking...you. " + message + " is invalid input. "
                        + "Please enter 'CHAT something' or EXIT: " + clientStatus);
                outputToClient.newLine();
                outputToClient.flush();
            }
            if (clientStatus == ClientStatus.INGAME) {
                break;
            }
        }
    }

    /**
     * Possible inputs from the client with the status INGAME and the following actions
     *
     * @throws IOException
     */
    public void gameInput() throws IOException {
        String message = inputFromClient.readLine();

        while (message != null && clientStatus == ClientStatus.INGAME) {
            String inputMessage[] = message.split(" ");

            if (message.startsWith("MOVE") && isParsable(inputMessage[1]) && isParsable(inputMessage[2]) && inputMessage.length == 3) {
                sgs.executeTurnMove(Integer.parseInt(inputMessage[1]), Integer.parseInt(inputMessage[2]));
            } else if (message.startsWith("PASS") && inputMessage.length == 1) {
                sgs.executeTurnPass();
            } else if (message.startsWith("TABLEFLIP") && inputMessage.length == 1) {
                sgs.executeTurnTableflip();
                outputToClient.write("TABLEFLIPPED" + message);
                outputToClient.newLine();
                outputToClient.flush();
                playAgain();
            } else if (message.startsWith("CHAT")) {
                sgs.chatToOtherPlayer(message);
            } else if (message.startsWith("EXIT")) {
                try {
                    this.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inputFromClient.close();
                outputToClient.close();
                System.exit(0);
            } else {
                outputToClient.write("WARNING gameInput: Must...resist...kicking...you. Message " + message + " is invalid input.");
                outputToClient.newLine();
                outputToClient.flush();
            }
            if (clientStatus == ClientStatus.PREGAME) {
            }
        }
    }

    /**
     * kicks a player from the server for making an illegal move
     *
     * @param clientID
     * @throws IOException
     */
    public void annihilatePlayer(int clientID) throws IOException {
        sgs.otherPlayerWins();
        outputToClient.write("You've been caught cheating, therefore you shall be annihilated!");
        server.pendingClients.get(this.getDim()).remove(clientID);
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
            System.out.println("CHAT Server: Illegal input " + name +
                    ", name requirements: \n- name < 20 characters \n- name may only consist out of digits and letters");
            return false;
        }
        writeToClient("CHAT server: Your name is: " + name);
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
     * @throws IOException
     */
    private boolean playAgain() throws IOException {
        boolean inputError = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        do {
            writeToClient("Play again? (Y/N)");
            try {
                String playAgain = input.readLine();
                if (playAgain.equals("Y") | playAgain.equals("y") | playAgain.equals("yes")) {
                    clientStatus = ClientStatus.PREGAME;
                    return true;
                } else if (playAgain.equals("N") | playAgain.equals("n") | playAgain.equals("no")) {
                    socket.close();
                    inputFromClient.close();
                    outputToClient.close();
                    return false;
                } else {
                    writeToClient("Wrong input (Y/N)");
                    inputError = true;
                }
            } catch (IOException e) {
                writeToClient("Wrong input (Y/N)");
                inputError = true;
            }
        } while (inputError);

        return false;
    }

    /**
     * general message writer from server to client
     *
     * @param message
     * @throws IOException
     */
    public synchronized void writeToClient(String message) {
        try {
            outputToClient.write("CHAT from server: " + message);
            outputToClient.newLine();
            outputToClient.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
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