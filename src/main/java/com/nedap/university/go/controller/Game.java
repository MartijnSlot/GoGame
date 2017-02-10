package com.nedap.university.go.controller;

import com.nedap.university.go.model.Board;
import com.nedap.university.go.model.Player;
import com.nedap.university.go.model.Position;
import com.nedap.university.go.model.Stone;
import com.nedap.university.go.viewer.GoGUIIntegrator;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for maintaining a GO game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class Game {

    private int numberPlayers = 2;
    private Board board;
    private Player[] players;
    public int currentPlayer;
    private Set<String> history = new HashSet<>();
    private boolean draw;
    private static GoGUIIntegrator gogui;

    public Game(int dim) {
        board = new Board(dim);
        players = new Player[numberPlayers];
        players[0] = new Player(Stone.BLACK);
        players[1] = new Player(Stone.WHITE);
        currentPlayer = 0;
        draw = false;
        gogui = new GoGUIIntegrator(false, true, dim);
        gogui.startGUI();
        gogui.setBoardSize(dim);
    }

    public Board getBoard() {
        return board;
    }

    /**
     * the 'move' turn of a player on the board. It puts a move for the current player on thge board
     * updates board TUI and GUI
     * writes history for the KO rule
     * gives the turn to the next player
     *
     * @param x
     * @param y
     */
    public void executeTurn(int x, int y) {
        updateTUI();
        players[currentPlayer].makeMove(board, new Position(x, y));
        addToGUI(x, y);
        autoRemove(x, y);
        writeHistory();
        currentPlayer = (currentPlayer + 1) % numberPlayers;
        updateTUI();
    }

    /**
     * the 'pass' turn of a player on the board.
     * if both players have passed; determine winner
     */
    public void passMove() {
        if (!players[(currentPlayer + 1) % numberPlayers].pass) {
            players[currentPlayer].passes();
            currentPlayer = (currentPlayer + 1) % numberPlayers;
        }
        if (players[(currentPlayer + 1) % numberPlayers].pass && players[currentPlayer].pass) {
            determineWinner();
        }

    }

    /**
     * determines the winner according to the score. Now only count stones
     */
    private void determineWinner() {
        if (board.countScore()[0] > board.countScore()[1]) {
            for (int i = 0; i < numberPlayers; i++) {
                if (players[i].getStone() == Stone.BLACK) players[i].isWinner();
            }
        }
        if (board.countScore()[1] > board.countScore()[0]) {
            for (int i = 0; i < numberPlayers; i++) {
                if (players[i].getStone() == Stone.WHITE) players[i].isWinner();
            }
        }
        if (board.countScore()[1] == board.countScore()[0]) {
            draw = true;
        }
    }

    /**
     * tableflip mechanism
     * sets a player to winner
     */
    public void tableflipMove() {
        players[currentPlayer].winner = false;
        players[(currentPlayer + 1) % numberPlayers].winner = true;
    }

    /**
     * reset board
     */
    public void reset() {
        this.board = new Board(board.getDim());
    }

    /**
     * replace the defending cluster stones (black, white) with EMPTY
     *
     * @param x
     * @param y
     */
    private void autoRemove(int x, int y) {
        Set<Position> a = new HashSet<>();
        a.add(new Position(x - 1, y));
        a.add(new Position(x + 1, y));
        a.add(new Position(x, y - 1));
        a.add(new Position(x, y + 1));
        for (Position p : a) {
            if (board.isPoint(p) && !board.isEmptyPoint(p) && board.numberOfLiberties(p) == 0) {
                for (Position q : board.defendingCluster(p)) {
                    board.setPoint(q, Stone.EMPTY);
                    removeFromGUI(x, y);
                }
            }
        }
        if (!board.isEmptyPoint(new Position(x, y)) && board.numberOfLiberties(new Position(x, y)) == 0) {
            for (Position r : board.defendingCluster(new Position(x, y))) {
                board.setPoint(r, Stone.EMPTY);
                removeFromGUI(x, y);
            }
        }
    }


    /**
     * writes the current boardStatus to history.
     */
    private void writeHistory() {
        history.add(this.board.toSimpleString());
    }

    /**
     * checks if the placement of a stone is in accordance with the <i>ko-rule</i>
     *
     * @param x
     * @param y
     * @return boolean
     */
    public boolean inKo(int x, int y) {
        boolean inKo = false;
        this.players[currentPlayer].makeMove(this.board, new Position(x, y));
        for (String b : history) {
            if (this.board.toSimpleString().equals(b)) {
                board.removePoint(new Position(x, y));
                inKo = true;
            }
        }
        board.removePoint(new Position(x, y));
        return inKo;
    }

    /**
     * checks if the game has a winner
     *
     * @return boolean
     */
    public boolean hasWinner() {
        return (players[0].winner | players[1].winner);
    }


    /**
     * adds a stone to the GUI, only the game is allowed to do this.
     *
     * @param x
     * @param y
     */
    public void addToGUI(int x, int y) {
        gogui.addStone(x, y, this.players[currentPlayer].getStone() == Stone.WHITE);
    }

    /**
     * removes a stone from the GUI, only the game is allowed to do this.
     *
     * @param x
     * @param y
     */
    private void removeFromGUI(int x, int y) {
        gogui.removeStone(x, y);
    }

    /**
     * Update the state of the board to the console!
     */
    private void updateTUI() {
        System.out.println("\nAwesome-o GO board: \n\n" + board.toString() + "\n");
    }


}
