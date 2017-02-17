package com.nedap.university.go.server;

import java.net.*;
import java.util.*;

import java.io.*;
import com.nedap.university.go.server.ClientHandler.ClientStatus;

/**
 * Class for creating a general server.
 * 
 * @author Martijn Slot
 * @version 1.0
 */

public class GoServer extends Thread {

	Socket socket;
	private ServerSocket serverSocket;
	Map<ClientHandler, Integer> clientHandlerMap = new HashMap<>();
	Map<Integer, List<ClientHandler>> pendingClients = new HashMap<>();
	Set<ClientHandler> clientSet = new HashSet<>();

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
		int clientCounter = 0;

		while (true) {
			try {
				clientCounter += 1; 
				if (clientCounter >= 500) {
					System.out.println("Too many Clients!, restart server!");
					socket.close();
					break;
				}
				ClientHandler newClient = new ClientHandler(serverSocket.accept(), this);
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
	
	public void chatToAllPlayers(String message) throws IOException {
		for (ClientHandler clientHandler : clientSet) {
            clientHandler.writeToClient(message);
		}
	}
	
	void clientEntry(ClientHandler ch) {
	    clientSet.add(ch);
    }

	/**
	 * enter the client into the server list, then into the server waiting list
	 * if there is another client with the same dimension, it will start a game
	 * @param client
	 * @param dim
	 * @throws IOException
	 */
	public void clientMatcher(ClientHandler client, int dim) throws IOException {
		ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
		clientHandlers.add(client);
		clientHandlerMap.put(client, dim);	

		while (client.getClientStatus() == ClientStatus.PREGAME) {
			if (pendingClients.containsKey(dim)) {
				pendingClients.get(dim).addAll(clientHandlers);
			} else {
				pendingClients.put(dim, clientHandlers);
			}

			client.setClientStatus(ClientStatus.WAITING);

			while (client.getClientStatus() == ClientStatus.WAITING) {
				for (int dimBoard : pendingClients.keySet()) {
					if (pendingClients.get(dimBoard).size() == 2) {
						Random r = new Random();
						int  n = r.nextInt(1);
						ClientHandler ch1 = pendingClients.get(dimBoard).get(n);
						ClientHandler ch2 = pendingClients.get(dimBoard).get(1-n);
						pendingClients.remove(dimBoard);
						ch1.setClientStatus(ClientStatus.INGAME);
						ch2.setClientStatus(ClientStatus.INGAME);
                        SingleGameServer singleGameServer = new SingleGameServer(ch1, ch2, dim);
                        ch1.setSingleGameServer(singleGameServer);
                        ch2.setSingleGameServer(singleGameServer);
					} break;
				} break;
			}
		}
	}
	
	/**
	 * removes a client from the server clientlist
	 * @param clientHandler
	 */
	public void removeClient(ClientHandler clientHandler) {
		try {
			clientHandlerMap.remove(clientHandler);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void statusWaitingToInitial(ClientHandler clientHandler) {
		clientHandler.setClientStatus(ClientStatus.PREGAME);
	    pendingClients.get(clientHandler.getDim()).remove(clientHandler);
	}

}
