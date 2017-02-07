package com.nedap.university.go.server;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.server.ClientHandler.ClientStatus;
import java.io.IOException;

/**
 * Class for creating a server for a single game.
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class SingleGameServer {

	private ClientHandler[] chs;
	private Game game;
	private int currentClient;

	
	/**
	 * Constructor: starts a game with two clientHandlers and sends out the ready signal
	 * @param a
	 * @param b
	 * @param dim
	 * @throws IOException
	 */
	public SingleGameServer(ClientHandler a, ClientHandler b, int dim) throws IOException {
		chs = new ClientHandler[2];
		this.chs[0] = a;
		this.chs[1] = b;

		String playerNames[] = new String[2];
		playerNames[0] = a.getClientName();
		playerNames[1] = b.getClientName();
		game = new Game(playerNames[0], playerNames[1], dim);

		String opponent1 = chs[1].getName();
		String color1 = "black";
		chs[0].sendReady(color1, opponent1, dim);

		String opponent2 = chs[0].getName();
		String color2 = "black";
		chs[1].sendReady(color2, opponent2, dim);
	}

	/**
	 * getter for the current client which turn it is
	 * @return int
	 */
	public int getCurrentClient() {
		return currentClient;
	}

	/**
	 * setter for the current client
	 * @param a
	 */
	public void setCurrentClient(int a) {
		this.currentClient = a;
	}
	
	/**
	 * executes a 'move'  turn, moves a stone on x (col), y (row)
	 * writes the move to all participating clients
	 * sets the players' statuses
	 * @param col
	 * @param row
	 * @throws IOException
	 */
	public void executeTurnMove(int col, int row) throws IOException {
		if (game.getBoard().isAllowed(col, row)) {
			game.executeTurn(col, row);
			setCurrentClient(game.currentPlayer);
			chs[(currentClient + 1) % 2].writeToClient("MOVE: ");
			chs[currentClient].setClientStatus(ClientStatus.WAITING);
			chs[(currentClient + 1) % 2].setClientStatus(ClientStatus.INGAME);
		}
	}

	/**
	 * executes a 'pass' turn
	 * writes the pass to all participating clients
	 * sets the players' statuses
	 * @throws IOException
	 */
	public void executeTurnPass() throws IOException {
		game.passMove();
		setCurrentClient(game.currentPlayer);
		chs[(currentClient + 1) % 2].writeToClient("PASSED");
		chs[currentClient].setClientStatus(ClientStatus.WAITING);
		chs[(currentClient + 1) % 2].setClientStatus(ClientStatus.INGAME);
	}

	/**
	 * executes a 'tableflip' turn
	 * writes the tableflip to opponent
	 * finishes the game
	 * @throws IOException
	 */
	public void executeTurnTableflip() throws IOException {
		chs[(currentClient + 1) % 2].writeToClient("TABLEFLIPPED");
		game.tableflipMove();
	}

	/**
	 * the other player wins
	 * writes the end move to opponent
	 * @throws IOException
	 */
	public void otherPlayerWins() throws IOException {
		chs[(currentClient + 1) % 2].writeToClient("END");
	}

	/**
	 * chat to the other player
	 * writes the chat to opponent
	 * @throws IOException
	 */
	public synchronized void chatToOtherPlayer(String message) throws IOException {
		chs[(currentClient + 1) % 2].writeToClient(message);
	}

}
