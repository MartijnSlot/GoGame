package com.nedap.university.go.server;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Class for creating a general server.
 *
 * @author Martijn Slot
 * @version 1.0
 */

public class GoServer extends Thread {

    Socket socket;
    private ServerSocket serverSocket;
    private Map<ClientHandler, Integer> clientHandlerMap = new HashMap<>();
    private Map<Integer, List<ClientHandler>> pendingClients = new HashMap<>();
    Set<ClientHandler> clientSet = new HashSet<>();
    private int clientCounter = 0;

    public GoServer(int port) {
        System.out.println("Starting server on port " + port);
        try {
            int maxClients = 50;
            serverSocket = new ServerSocket(port, maxClients);
        } catch (IOException e) {
            System.out.print("Could not listen on port " + port);
        }
    }

    @Override
    public void run() {
        System.out.println("Waiting for clients...");

        while (true) {
            try {
                ClientHandler newClient = new ClientHandler(serverSocket.accept(), this);
                clientCounter += 1;
                if (clientCounter >= 500) {
                    System.out.println("Too many Clients!, restart server!");
                    socket.close();
                    break;
                }
                System.out.println("Client Accepted! Client count: " + clientCounter);
                newClient.start();
            } catch (IOException e) {
                System.out.println("Cannot accept client.");
            }
        }
    }

    /**
     * Sends out a chatmessage to all players on server
     * @param message
     * @throws IOException
     */

    public void chatToAllPlayers(String message) {
        for (ClientHandler clientHandler : clientSet) {
            clientHandler.writeToClient(message);
        }
    }

    /**
     * enter the client into the server list, then into the server waiting list
     * if there is another client with the same dimension, it will start a game
     * @param client
     * @param dim
     * @throws IOException
     */
    void addToWaitingList(ClientHandler client, int dim) {
        ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        clientHandlers.add(client);
        clientHandlerMap.put(client, dim);

        if (pendingClients.containsKey(dim)) {
            pendingClients.get(dim).addAll(clientHandlers);
        } else {
            pendingClients.put(dim, clientHandlers);
        }

        client.setClientStatus(ClientStatus.WAITING);
        matchWaitingPlayers();
    }

    private void matchWaitingPlayers() {
        for (int dimBoard : pendingClients.keySet()) {
            if (pendingClients.get(dimBoard).size() == 2) {
                startNewGame(dimBoard);
                break;
            }
        }
    }

    private void startNewGame(int dimBoard) {
        ClientHandler ch1 = pendingClients.get(dimBoard).get(0);
        ClientHandler ch2 = pendingClients.get(dimBoard).get(1);
        pendingClients.remove(dimBoard);

        ch1.setClientStatus(ClientStatus.INGAME_TURN);
        ch2.setClientStatus(ClientStatus.INGAME_NOT_TURN);
        SingleGameServer singleGameServer = null;
        try {
            singleGameServer = new SingleGameServer(ch1, ch2, dimBoard);
            singleGameServer.startGame(ch1, ch2, dimBoard);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ch1.setSingleGameServer(singleGameServer);
        ch2.setSingleGameServer(singleGameServer);
    }

    /**
     * removes a client from the server clientlist
     * @param clientHandler to remove
     */
    void eraseClient(ClientHandler clientHandler) {
        try {
            clientCounter -= 1;
            System.out.println("Client removed! Client count: " + clientCounter);
            clientHandlerMap.remove(clientHandler);
            pendingClients.get(clientHandler.getDim()).remove(clientHandler);
            clientSet.remove(clientHandler);
        } catch (NullPointerException npe) {
            System.out.println("Client " + clientHandler.getClientName() + " erased.");
        }
    }

    void statusWaitingToInitial(ClientHandler clientHandler) {
        pendingClients.get(clientHandler.getDim()).remove(clientHandler);
    }

}
