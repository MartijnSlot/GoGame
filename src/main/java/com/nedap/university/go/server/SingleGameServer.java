package com.nedap.university.go.server;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.gocommands.Protocol;

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
		chs = new ClientHandler[NUMBER_OF_PLAYERS];
		this.chs[BLACK] = a;
		this.chs[WHITE] = b;
		game = new Game(dim);

	}

	void startGame(ClientHandler a, ClientHandler b, int dim) {
		String playerNames[] = new String[NUMBER_OF_PLAYERS];
		playerNames[BLACK] = a.getClientName();
		playerNames[WHITE] = b.getClientName();

		String opponentOfBLACK = playerNames[WHITE];
		chs[BLACK].setColor("black");
		chs[BLACK].writeToClient("READY" + Protocol.DELIMITER + "black" + Protocol.DELIMITER + opponentOfBLACK + Protocol.DELIMITER + dim);

		String opponentOfWHITE = playerNames[BLACK];
		chs[WHITE].setColor("white");
		chs[WHITE].writeToClient("READY" + Protocol.DELIMITER + "white" + Protocol.DELIMITER + opponentOfWHITE + Protocol.DELIMITER + dim);

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
					sendToPlayers("CHAT server - white wins. Kudos, " + chs[WHITE].getClientName());
					sendToPlayers("END " + endGame());
					chs[BLACK].setClientStatus(ClientStatus.PREGAME);
                    chs[WHITE].setClientStatus(ClientStatus.PREGAME);
                    break;
				case "black":
					sendToPlayers("CHAT server - black wins. Kudos, " + chs[BLACK].getClientName());
					sendToPlayers("END " + endGame());
                    chs[BLACK].setClientStatus(ClientStatus.PREGAME);
                    chs[WHITE].setClientStatus(ClientStatus.PREGAME);
					break;
				case "draw":
					sendToPlayers("CHAT server - Its a draw, you seem to be evenly matched.");
					sendToPlayers("END " + endGame());
                    chs[BLACK].setClientStatus(ClientStatus.PREGAME);
                    chs[WHITE].setClientStatus(ClientStatus.PREGAME);
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
        chs[BLACK].setClientStatus(ClientStatus.PREGAME);
        chs[WHITE].setClientStatus(ClientStatus.PREGAME);
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
		if (clientHandler.equals(chs[BLACK])) {
			chs[WHITE].setClientStatus(ClientStatus.INGAME_TURN);
			chs[WHITE].writeToClient("CHAT server - your turn, white. ");
		} else {
			chs[BLACK].setClientStatus(ClientStatus.INGAME_TURN);
			chs[BLACK].writeToClient("CHAT server - your turn, black. ");
		}
	}

	String executeScore() {
		game.countScore();
		return game.getScores();
	}
}
