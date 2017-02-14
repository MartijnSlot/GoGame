package com.nedap.university.go.server;

import com.nedap.university.go.controller.Game;
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
		String color1 = "black";
		chs[0].sendReady(color1, opponent1, dim);
		chs[0].setTurn(true);
        chs[0].askInput();

		String opponent2 = a.getClientName();
		String color2 = "white";
		chs[1].sendReady(color2, opponent2, dim);
		chs[1].setTurn(false);
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
		if (game.getBoard().isAllowed(x, y)) {
			game.executeTurn(x, y);
			chatToGamePlayers("VALID " + x + " " + y);
			setCurrentClient(game.getCurrentPlayer());
			chs[currentClient].setTurn(true);
			chs[currentClient].writeToClient("\nCHAT It is now your turn. Options: \nMOVE x y\nPASS\nCHAT\nTABLEFLIP\nEXIT");
			chs[otherClient].setTurn(false);
			chs[otherClient].writeToClient("\nCHAT It is not your turn. Options: CHAT\nEXIT");;
		} else {
            chatToGamePlayers("INVALID");
			chs[currentClient].annihilatePlayer();
		}
	}

	/**
	 * executes a 'pass' turn
	 * writes the pass to all participating clients
	 * sets the players' statuses
	 * @throws IOException
	 */
	void executeTurnPass() throws IOException {
		game.passMove();
		setCurrentClient(game.getCurrentPlayer());
		chs[otherClient].writeToClient("PASSED");
		chs[currentClient].setTurn(true);
		chs[otherClient].setTurn(false);
	}

	/**
	 * executes a 'tableflip' turn
	 * writes the tableflip to opponent
	 * finishes the game
	 * @throws IOException
	 */
	void executeTurnTableflip() throws IOException {
		chs[otherClient].writeToClient("TABLEFLIPPED");
		game.tableflipMove();
	}

	/**
	 * the other player wins
	 * writes the end move to opponent
	 * @throws IOException
	 */
	void otherPlayerWins() throws IOException {
		chs[otherClient].writeToClient("END : " + game.getBoard().countScore()[0] + game.getBoard().countScore()[1] );
	}

	/**
	 * chat to the other player
	 * writes the chat to opponent
	 * @throws IOException
	 */
	void chatToGamePlayers(String message) throws IOException {
		System.out.println(message);
        for (ClientHandler ch : chs) {
            ch.writeToClient(message);
        }
	}
}
