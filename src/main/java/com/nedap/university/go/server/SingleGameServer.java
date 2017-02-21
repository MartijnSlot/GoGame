package com.nedap.university.go.server;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.model.Stone;

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
	private int currentClient = 0;
	private int otherClient = (currentClient + 1) % 2;

	
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
		game = new Game(dim);

		String opponent1 = b.getClientName();
		String color1 = game.players[0].getStone().toString();
		chs[0].sendReady(color1, opponent1, dim);

		String opponent2 = a.getClientName();
		String color2 = game.players[1].getStone().toString();
		chs[1].sendReady(color2, opponent2, dim);
	}

	/**
	 * setter for the current client
	 * @param a
	 */
	void setCurrentClient(int a) {
		this.currentClient = a;
	}
	
	/**
	 * executes a 'move'  turn, moves a stone on x (col), y (row)
	 * writes the move to all participating clients
	 * sets the players' statuses
	 * @param x
	 * @param y
	 * @throws IOException
	 */
	void executeTurnMove(int x, int y) throws IOException {

	}

	/**
	 * executes a 'pass' turn
	 * writes the pass to all participating clients
	 * sets the players' statuses
	 * @throws IOException
	 */
	void executeTurnPass() throws IOException {
	}


	/**
	 * executes a 'tableflip' turn
	 * writes the tableflip to opponent
	 * finishes the game
	 * @throws IOException
	 */
	void executeTurnTableflip() throws IOException {

	}

	/**
	 * the other player wins
	 * writes the end move to opponent
	 * @throws IOException
	 */
	void otherPlayerWins() throws IOException {
	}

	/**
	 * chat to the other player
	 * writes the chat to opponent
	 * @throws IOException
	 */
	void chatToGamePlayers(String message) throws IOException {

	}
}
