package com.nedap.university.go.server;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.model.Player;
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
		String color1 = game.getPlayer1().getStone().toString();

		String opponent2 = a.getClientName();
		String color2 = game.getPlayer2().getStone().toString();
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
	void executeTurnTableflip(ClientHandler clientHandler) {
		game.tableflipMove(clientHandler.getColorInt());
		chatToGamePlayers("CHAT server - " + clientHandler.getClientName() + " has flipped.\n");
		chatToGamePlayers("END " + endGame());
	}

	private String endGame() {
		String endScores = null;
		for (int i : game.getScores()) {
			endScores = endScores + i + " ";
		}
		endScores = endScores.trim();
		return endScores;
	}


	/**
	 * chat to the all game players
	 *
	 * @throws IOException
	 */
	void chatToGamePlayers(String message) {
		for (int i = 0; i < chs.length; i++) {
			chs[i].writeToClient(message);
		}
	}
}
