package com.nedap.university.go.server

import com.nedap.university.go.controller.Game
import com.nedap.university.go.gocommands.Protocol

import java.io.IOException

/**
 * Class for creating a server for a single game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
class SingleGameServer @Throws(IOException::class)
constructor(a: ClientHandler, b: ClientHandler, dim: Int) {

    private val chs: Array<ClientHandler>
    private val game: Game

    init {
        chs = arrayOfNulls<ClientHandler>(NUMBER_OF_PLAYERS)
        this.chs[BLACK] = a
        this.chs[WHITE] = b
        game = Game(dim)

    }

    internal fun startGame(a: ClientHandler, b: ClientHandler, dim: Int) {
        val playerNames = arrayOfNulls<String>(NUMBER_OF_PLAYERS)
        playerNames[BLACK] = a.getClientName()
        playerNames[WHITE] = b.getClientName()

        val opponentOfBLACK = playerNames[WHITE]
        chs[BLACK].setColor("black")
        chs[BLACK].writeToClient("READY" + Protocol.DELIMITER + "black" + Protocol.DELIMITER + opponentOfBLACK + Protocol.DELIMITER + dim)

        val opponentOfWHITE = playerNames[BLACK]
        chs[WHITE].setColor("white")
        chs[WHITE].writeToClient("READY" + Protocol.DELIMITER + "white" + Protocol.DELIMITER + opponentOfWHITE + Protocol.DELIMITER + dim)

        sendToPlayers("CHAT server - Let's make GO great again!")
    }

    /**
     * executes a 'move'  turn, moves a stone on x (col), y (row)
     * writes the move to all participating clients
     * sets the players' statuses
     * @param x dimension
     * @param y dimension
     */
    internal fun executeTurnMove(x: Int, y: Int, clientHandler: ClientHandler) {
        if (game.moveAllowed(x, y)) {
            game.doMove(x, y)
            sendToPlayers("VALID " + clientHandler.getColor() + " " + x + " " + y)
            switchTurns(clientHandler)
        } else {
            sendToPlayers("INVALID " + clientHandler.getColor() + " " + clientHandler.getClientName() + " has made illegal move. " + x + " " + y)
            clientHandler.annihilatePlayer()

        }
    }

    /**
     * executes a 'pass' turn
     * writes the pass to all participating clients
     * sets the players' statuses
     */
    internal fun executeTurnPass(clientHandler: ClientHandler) {
        game.passMove()
        sendToPlayers("PASSED " + clientHandler.getColor())
        if (game.getWinner() != null) {
            when (game.getWinner()) {
                "white" -> {
                    sendToPlayers("CHAT server - white wins. Kudos, " + chs[WHITE].getClientName())
                    sendToPlayers("END " + endGame())
                    chs[BLACK].setClientStatus(ClientStatus.PREGAME)
                    chs[WHITE].setClientStatus(ClientStatus.PREGAME)
                }
                "black" -> {
                    sendToPlayers("CHAT server - black wins. Kudos, " + chs[BLACK].getClientName())
                    sendToPlayers("END " + endGame())
                    chs[BLACK].setClientStatus(ClientStatus.PREGAME)
                    chs[WHITE].setClientStatus(ClientStatus.PREGAME)
                }
                "draw" -> {
                    sendToPlayers("CHAT server - Its a draw, you seem to be evenly matched.")
                    sendToPlayers("END " + endGame())
                    chs[BLACK].setClientStatus(ClientStatus.PREGAME)
                    chs[WHITE].setClientStatus(ClientStatus.PREGAME)
                }
                else -> sendToPlayers("CHAT server - Hier mag ie niet komen na het passen.")
            }
        } else {
            switchTurns(clientHandler)
        }
    }


    /**
     * executes a 'tableflip' turn
     * writes the tableflip to opponent
     * finishes the game
     */
    internal fun executeTurnTableflip(clientHandler: ClientHandler) {
        game.tableflipMove(clientHandler.getColor())
        sendToPlayers("CHAT server - " + clientHandler.getClientName() + " has totally flipped.\n")
        sendToPlayers("END " + endGame())
        chs[BLACK].setClientStatus(ClientStatus.PREGAME)
        chs[WHITE].setClientStatus(ClientStatus.PREGAME)
    }

    /**
     * get the scores from the game and put them in a nice string
     * @return String of endScores
     */
    private fun endGame(): String {
        return game.getScores()
    }


    /**
     * chat to the all game players
     *
     */
    internal fun sendToPlayers(message: String) {
        for (ch in chs) {
            ch.writeToClient(message)
        }
    }

    private fun switchTurns(clientHandler: ClientHandler) {
        clientHandler.setClientStatus(ClientStatus.INGAME_NOT_TURN)
        if (clientHandler.equals(chs[BLACK])) {
            chs[WHITE].setClientStatus(ClientStatus.INGAME_TURN)
            chs[WHITE].writeToClient("CHAT server - your turn, white. ")
        } else {
            chs[BLACK].setClientStatus(ClientStatus.INGAME_TURN)
            chs[BLACK].writeToClient("CHAT server - your turn, black. ")
        }
    }

    internal fun executeScore(): String {
        game.countScore()
        return game.getScores()
    }

    companion object {
        private val BLACK = 0
        private val WHITE = 1
        private val NUMBER_OF_PLAYERS = 2
    }
}
