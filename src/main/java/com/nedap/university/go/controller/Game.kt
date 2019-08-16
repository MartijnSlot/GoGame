package com.nedap.university.go.controller

import com.nedap.university.go.model.Board
import com.nedap.university.go.model.Player
import com.nedap.university.go.model.Position
import com.nedap.university.go.model.Stone

import java.util.HashSet

/**
 * Class for maintaining a GO game. TODO player nubmering
 *
 * @author Martijn Slot
 * @version 1.0
 */
class Game(dim: Int) {

    private val numberPlayers = 2
    var board: Board? = null
        private set
    private val players: Array<Player>
    private var currentPlayer: Int = 0
    private val history = HashSet()
    var autoRemoveSet: Set<Position> = HashSet()
    var winner: String? = null
        private set

    /**
     * determines the winner according to the score.
     */

    val scores: String
        get() = board!!.getBlackScore() + " " + board!!.getWhiteScore()

    init {
        board = Board(dim)
        players = arrayOfNulls<Player>(numberPlayers)
        players[0] = Player(Stone.BLACK)
        players[1] = Player(Stone.WHITE)
        currentPlayer = 0
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
    fun doMove(x: Int, y: Int) {
        players[currentPlayer].makeMove(board, Position(x, y))
        autoRemove(x, y)
        writeHistory()
        currentPlayer = (currentPlayer + 1) % numberPlayers
        updateTUI()
    }


    /**
     * the 'pass' turn of a player on the board.
     * if both players have passed; determine winner
     */
    fun passMove() {
        players[currentPlayer].passes()
        if (players[currentPlayer].getStone() === Stone.WHITE && players[(currentPlayer + 1) % numberPlayers].getPass()) {
            countScore()
            determineWinner()
        }
        currentPlayer = (currentPlayer + 1) % numberPlayers
    }


    fun countScore() {
        board!!.countScore()
    }

    private fun determineWinner() {
        if (board!!.getBlackScore() > board!!.getWhiteScore()) {
            players[0].isWinner()
            winner = "black"
        }
        if (board!!.getBlackScore() < board!!.getWhiteScore()) {
            players[1].isWinner()
            winner = "white"
        }
        if (board!!.getBlackScore() === board!!.getWhiteScore()) {
            winner = "draw"
        }
    }

    /**
     * tableflip mechanism
     * sets a player to winner
     */
    fun tableflipMove(color: String) {
        players[if (color.equals("black")) 1 else 0].winner = true
        players[if (color.equals("white")) 0 else 1].winner = true
        board!!.countScoreTableflip()
    }

    /**
     * reset board
     */
    fun reset() {
        this.board = Board(board!!.getDim())
    }

    /**
     * replace the defending cluster stones (black, white) with EMPTY
     *
     * @param x
     * @param y
     */
    fun autoRemove(x: Int, y: Int) {
        val a = HashSet()
        a.add(Position(x - 1, y))
        a.add(Position(x + 1, y))
        a.add(Position(x, y - 1))
        a.add(Position(x, y + 1))
        for (p in a) {
            if (board!!.isPoint(p) && !board!!.isEmptyPoint(p) && board!!.numberOfLiberties(p) === 0) {
                for (q in board!!.defendingCluster(p)) {
                    board!!.setPoint(q, Stone.EMPTY)
                    autoRemoveSet.add(q)
                }
            }
        }
        if (!board!!.isEmptyPoint(Position(x, y)) && board!!.numberOfLiberties(Position(x, y)) === 0) {
            for (r in board!!.defendingCluster(Position(x, y))) {
                board!!.setPoint(r, Stone.EMPTY)
                autoRemoveSet.add(r)
            }
        }
    }


    /**
     * writes the current boardStatus to history.
     */
    private fun writeHistory() {
        history.add(this.board!!.toSimpleString())
    }

    /**
     * checks if the placement of a stone exists, is not occupied and in accordance with the *ko-rule*
     *
     * @param x int dim
     * @param y int dim
     * @return allowed
     */
    fun moveAllowed(x: Int, y: Int): Boolean {
        return board!!.isAllowed(x, y) && !inKo(x, y)
    }

    /**
     * checks if the placement of a stone is in accordance with the *ko-rule*
     *
     * @param x
     * @param y
     * @return boolean
     */
    fun inKo(x: Int, y: Int): Boolean {
        var inKo = false
        this.players[currentPlayer].makeMove(this.board, Position(x, y))
        autoRemove(x, y)
        for (b in history) {
            if (this.board!!.toSimpleString().equals(b)) {
                board!!.removePoint(Position(x, y))
                inKo = true
            }
        }
        board!!.removePoint(Position(x, y))
        return inKo
    }

    /**
     * Update the state of the board to the console!
     */
    fun updateTUI() {
        System.out.println("\nAwesome-o GO board: \n\n" + board!!.toString() + "\n")
    }
}
