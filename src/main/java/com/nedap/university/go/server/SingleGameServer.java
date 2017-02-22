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
		game = new Game(dim);

	}

	/**
	 * setter for the current client
	 * @param a
	 */
	void setCurrentClient(int a) {
		this.currentClient = a;
	}

	void startGame(ClientHandler a, ClientHandler b, int dim) {
        String playerNames[] = new String[2];
        playerNames[0] = a.getClientName();
        playerNames[1] = b.getClientName();

        String opponentOf0 = playerNames[1];
        chs[0].setColor("black");
        chs[0].writeToClient("READY black " + opponentOf0 + dim);

        String opponentOf1 = playerNames[0];
        chs[1].setColor("white");
        chs[0].writeToClient("READY white " + opponentOf1 + dim);
    }

	/**
	 * executes a 'move'  turn, moves a stone on x (col), y (row)
	 * writes the move to all participating clients
	 * sets the players' statuses
	 * @param x dimension
	 * @param y dimension
	 */
	void executeTurnMove(int x, int y, ClientHandler clientHandler) {
	    if (game.moveAllowed(x, y)) {
	        game.executeTurn(x, y);
	        sendToPlayers("VALID " + clientHandler.getColor() + x + y);
	        switchTurns(clientHandler);
	    } else {
            sendToPlayers("INVALID " + clientHandler.getColor() + x + y);
            clientHandler.annihilatePlayer();

        }
	}

    /**
	 * executes a 'pass' turn
	 * writes the pass to all participating clients
	 * sets the players' statuses
	 */
	void executeTurnPass(ClientHandler clientHandler) {
        game.passMove();
        if (!game.getDraw() || game.hasWinner()) {
            endGame();
        }
        switchTurns(clientHandler);
	}


	/**
	 * executes a 'tableflip' turn
	 * writes the tableflip to opponent
	 * finishes the game
	 */
	void executeTurnTableflip(ClientHandler clientHandler) {
		game.tableflipMove(clientHandler.getColor());
		sendToPlayers("CHAT server - " + clientHandler.getClientName() + " has flipped.\n");
		sendToPlayers("END " + endGame());
	}

	/**
	 * get the scores from the game and put them in a nice string
	 * @return String of endScores
	 */
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
	 */
	void sendToPlayers(String message) {
		for (ClientHandler ch : chs) {
			ch.writeToClient(message);
		}
	}

    private void switchTurns(ClientHandler clientHandler) {
        clientHandler.setClientStatus(ClientStatus.INGAME_NOT_TURN);
	    if (clientHandler.equals(chs[0])) {
            chs[1].setClientStatus(ClientStatus.INGAME_TURN);
            chs[1].writeToClient("CHAT server - your turn, white");
        } else {
            chs[0].setClientStatus(ClientStatus.INGAME_TURN);
            chs[0].writeToClient("CHAT server - your turn, black");
        }
    }
}
