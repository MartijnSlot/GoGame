package com.nedap.university.go.model

/**
 * Class for creating a player in a GO game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
class Player(val stone: Stone) {
    var pass: Boolean = false
        private set
    var isWinner: Boolean = false

    init {
        this.pass = false
        this.isWinner = false
    }

    fun makeMove(board: Board, pos: Position) {
        board.setPoint(pos, this.stone)
        pass = false
    }

    fun passes() {
        this.pass = true
    }
}


