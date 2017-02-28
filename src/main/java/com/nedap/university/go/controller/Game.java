package com.nedap.university.go.controller;

import com.nedap.university.go.model.Board;
import com.nedap.university.go.model.Player;
import com.nedap.university.go.model.Position;
import com.nedap.university.go.model.Stone;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for maintaining a GO game. TODO player nubmering
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class Game {

    private int numberPlayers = 2;
    private Board board;
    private Player[] players;
    private int currentPlayer;
    private Set<String> history = new HashSet<>();
    public Set<Position> autoRemoveSet = new HashSet<>();
    private String winner = null;

    public Game(int dim) {
        board = new Board(dim);
        players = new Player[numberPlayers];
        players[0] = new Player(Stone.BLACK);
        players[1] = new Player(Stone.WHITE);
        currentPlayer = 0;
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
    public void doMove(int x, int y) {
        players[currentPlayer].makeMove(board, new Position(x, y));
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
        players[currentPlayer].passes();
        if (players[currentPlayer].getStone() == Stone.WHITE && players[(currentPlayer + 1) % numberPlayers].getPass()) {
            countScore();
            determineWinner();
            }
        currentPlayer = (currentPlayer + 1) % numberPlayers;
        }
    

    public void countScore(){
        board.countScore();
    }

    /**
     * determines the winner according to the score.
     */

    public String getScores() {
        return board.getBlackScore() + " " + board.getWhiteScore();
    }

    private void determineWinner() {
        if (board.getBlackScore() > board.getWhiteScore()) {
            players[0].isWinner();
            winner = "black";
        }
        if (board.getBlackScore() < board.getWhiteScore()) {
            players[1].isWinner();
            winner = "white";
        }
        if (board.getBlackScore() == board.getWhiteScore()) {
            winner = "draw";
        }
    }

    /**
     * tableflip mechanism
     * sets a player to winner
     */
    public void tableflipMove(String color) {
        players[color.equals("black") ? 1 : 0].winner = true;
        players[color.equals("white") ? 0 : 1].winner = true;
        board.countScoreTableflip();
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
    public void autoRemove(int x, int y) {
        Set<Position> a = new HashSet<>();
        a.add(new Position(x - 1, y));
        a.add(new Position(x + 1, y));
        a.add(new Position(x, y - 1));
        a.add(new Position(x, y + 1));
        for (Position p : a) {
            if (board.isPoint(p) && !board.isEmptyPoint(p) && board.numberOfLiberties(p) == 0) {
                for (Position q : board.defendingCluster(p)) {
                    board.setPoint(q, Stone.EMPTY);
                    autoRemoveSet.add(q);
                }
            }
        }
        if (!board.isEmptyPoint(new Position(x, y)) && board.numberOfLiberties(new Position(x, y)) == 0) {
            for (Position r : board.defendingCluster(new Position(x, y))) {
                board.setPoint(r, Stone.EMPTY);
                autoRemoveSet.add(r);
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
     * checks if the placement of a stone exists, is not occupied and in accordance with the <i>ko-rule</i>
     *
     * @param x int dim
     * @param y int dim
     * @return allowed
     */
    public boolean moveAllowed(int x, int y) {
        return (board.isAllowed(x, y) && !inKo(x, y));
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
        autoRemove(x, y);
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
     * Update the state of the board to the console!
     */
    public void updateTUI() {
        System.out.println("\nAwesome-o GO board: \n\n" + board.toString() + "\n");
    }

    public String getWinner() {
        return winner;
    }
}
