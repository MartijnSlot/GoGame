package com.nedap.university.go.server;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.gocommands.Protocol;
import com.nedap.university.go.model.Stone;

import java.io.IOException;

/**
 * Class for creating a server for a single game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class SingleGameServer {
	private static final int BLACK = 0;
	private static final int WHITE = 1;
	private static final int NUMBER_OF_PLAYERS = 2;

	private ClientHandler[] chs;
	private Game game;

	public SingleGameServer(ClientHandler a, ClientHandler b, int dim) throws IOException {
		chs = new ClientHandler[2];
		this.chs[0] = a;
		this.chs[1] = b;
		game = new Game(dim);

	}

	void startGame(ClientHandler a, ClientHandler b, int dim) {
		String playerNames[] = new String[2];
		playerNames[0] = a.getClientName();
		playerNames[1] = b.getClientName();

		String opponentOf0 = playerNames[1];
		chs[0].setColor("black");
		chs[0].writeToClient("READY" + Protocol.DELIMITER + "black" + Protocol.DELIMITER + opponentOf0 + Protocol.DELIMITER + dim);

		String opponentOf1 = playerNames[0];
		chs[1].setColor("white");
		chs[1].writeToClient("READY" + Protocol.DELIMITER + "white" + Protocol.DELIMITER + opponentOf1 + Protocol.DELIMITER + dim);

		sendToPlayers("CHAT server - Let's make GO great again!");
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
			game.doMove(x, y);
			sendToPlayers("VALID " + clientHandler.getColor() + " " + x + " " + y);
			switchTurns(clientHandler);
		} else {
			sendToPlayers("INVALID " + clientHandler.getColor() + " " + clientHandler.getClientName() + " has made illegal move. " + x + " " + y);
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
		sendToPlayers("PASSED " + clientHandler.getColor());
		if (game.getWinner() != null) {
			switch (game.getWinner()) {
				case "white":
					sendToPlayers("CHAT server - white wins. Kudos, " + chs[1].getClientName());
					sendToPlayers("END " + endGame());
					chs[0].setClientStatus(ClientStatus.PREGAME);
                    chs[1].setClientStatus(ClientStatus.PREGAME);
                    break;
				case "black":
					sendToPlayers("CHAT server - black wins. Kudos, " + chs[0].getClientName());
					sendToPlayers("END " + endGame());
                    chs[0].setClientStatus(ClientStatus.PREGAME);
                    chs[1].setClientStatus(ClientStatus.PREGAME);
					break;
				case "draw":
					sendToPlayers("CHAT server - Its a draw, you seem to be evenly matched.");
					sendToPlayers("END " + endGame());
                    chs[0].setClientStatus(ClientStatus.PREGAME);
                    chs[1].setClientStatus(ClientStatus.PREGAME);
					break;
				default:
					sendToPlayers("CHAT server - Hier mag ie niet komen na het passen.");
					break;
			}
		} else {
			switchTurns(clientHandler);
		}
	}


	/**
	 * executes a 'tableflip' turn
	 * writes the tableflip to opponent
	 * finishes the game
	 */
	void executeTurnTableflip(ClientHandler clientHandler) {
		game.tableflipMove(clientHandler.getColor());
		sendToPlayers("CHAT server - " + clientHandler.getClientName() + " has totally flipped.\n");
		sendToPlayers("END " + endGame());
	}

	/**
	 * get the scores from the game and put them in a nice string
	 * @return String of endScores
	 */
	private String endGame() {
		return game.getScores();
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
			chs[1].writeToClient("CHAT server - your turn, white, ");
		} else {
			chs[0].setClientStatus(ClientStatus.INGAME_TURN);
			chs[0].writeToClient("CHAT server - your turn, black, ");
		}
	}

	public String executeScore() {
		game.countScore();
		return game.getScores();
	}
}
