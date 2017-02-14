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
    private boolean turn = false;

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
                        writeToClient("ClientStatus " + clientStatus);
                        break;
                    case WAITING:
                        waitingInput();
                        writeToClient("ClientStatus " + clientStatus);
                        break;
                    case INGAME:
                        gameInput();
                        writeToClient("ClientStatus " + clientStatus);
                        break;
                    default:
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

    private void setDim(int d) {
        dim = d;
    }

    /**
     * sends out the READY message with playercolor, opponentname and boardsize.
     *
     * @param color
     * @param opponent
     * @param boardSize
     * @throws IOException
     */
    synchronized void sendReady(String color, String opponent, int boardSize) throws IOException {
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

        writeToClient("Please enter command PLAYER name, GO dim, CHAT message or EXIT: ");
        String message = inputFromClient.readLine();
        while (message != null) {
            String inputMessage[] = message.split(" ");
            if (message.startsWith("PLAYER") && inputMessage.length == 2 && checkName(inputMessage[1]) && clientName == null) {
                clientName = inputMessage[1];
                server.clientEntry(this);
                writeToClient("Your name is " + clientName);
                System.out.println(clientName + " has entered the arena!");
                break;
            } else if (message.startsWith("GO") && inputMessage.length == 2 && checkDim(inputMessage[1])) {
                setDim(Integer.parseInt(inputMessage[1]));
                writeToClient("Dimension entered: " + dim);
                System.out.println("Game waiting: " + clientName + " " + dim);
                server.clientMatcher(this, dim);
                break;
            } else if (message.startsWith("CHAT")) {
                server.chatToAllPlayers(clientName + ": " + message);
                System.out.println(clientName + ": " + message);
                break;
            } else if (message.startsWith("EXIT") && inputMessage.length == 1) {
                System.out.println(clientName + " has disconnected");
                try {
                    this.join();
                    outputToClient.close();
                    inputFromClient.close();
                    server.removeClient(this);
                } catch (InterruptedException e) {
                    System.out.println("disconnection failed: restart server.");
                }
            } else {
                outputToClient.write("WARNING PreGameInput: Must...resist...kicking...you." + message + " is invalid input. "
                        + "Please enter PLAYER name, GO dim, CHAT message or EXIT: " + clientStatus + " ");
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
                server.chatToAllPlayers(clientName + ": " + message);
                System.out.println(clientName + ": " + message);
                break;
            } else if (message.startsWith("EXIT") && inputMessage.length == 1) {
                System.out.println(clientName + " has disconnected");
                try {
                    this.join();
                    outputToClient.close();
                    inputFromClient.close();
                    server.removeClient(this);
                } catch (InterruptedException e) {
                    System.out.println("disconnection failed: restart server.");
                }
            } else if (message.startsWith("CANCEL") && inputMessage.length == 1) {
                System.out.println(clientName + " has disconnected");
                server.statusWaitingToInitial(this);
                clientStatus = ClientStatus.PREGAME;
                break;
            }
            else {
                outputToClient.write("WARNING waitingInput: Must...resist...kicking...you. " + message + " is invalid input. "
                        + "Please enter 'CHAT something' or EXIT: " + clientStatus);
                outputToClient.newLine();
                outputToClient.flush();
                break;
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

            if (message.startsWith("MOVE") && isParsable(inputMessage[1]) && isParsable(inputMessage[2]) && inputMessage.length == 3 && turn) {
                sgs.executeTurnMove(Integer.parseInt(inputMessage[1]), Integer.parseInt(inputMessage[2]));
                break;
            } else if (message.startsWith("PASS") && inputMessage.length == 1 && turn) {
                sgs.executeTurnPass();
                break;
            } else if (message.startsWith("TABLEFLIP") && inputMessage.length == 1 && turn) {
                sgs.executeTurnTableflip();
                writeToClient("TABLEFLIPPED" + message);
                playAgain();
            } else if (message.startsWith("CHAT")) {
                sgs.chatToGamePlayers(message);
                break;
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
                break;
            }
            if (clientStatus == ClientStatus.PREGAME) {
                playAgain();
            }
        }
    }

    /**
     * kicks a player from the server for making an illegal move
     *
     * @throws IOException
     */
    public void annihilatePlayer() throws IOException {
        sgs.otherPlayerWins();
        writeToClient("You've been caught cheating, therefore you shall be annihilated!");
        server.pendingClients.get(this.getDim()).remove(this);
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
                System.out.println("dimension is not OK");
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
    synchronized void writeToClient(String message) {
        try {
            outputToClient.write("CHAT from server: " + message);
            outputToClient.newLine();
            outputToClient.flush();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    void setTurn(boolean turn) {
        this.turn = turn;
        if (turn) writeToClient("It is your turn, " + clientName);
        else writeToClient("It is not your turn.");
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