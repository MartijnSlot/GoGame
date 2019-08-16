package com.nedap.university.go.gocommands

import com.nedap.university.go.aiStrategies.Strategy

/**
 * Created by martijn.slot on 21/02/2017.
 */
interface Protocol {
    companion object {

        /**
         * TABLEFLIPPED color
         *
         * message from the server to the client that the color client surrendered
         */
        val TABLEFLIPPED = "TABLEFLIPPED"

        /**
         * SCORE
         *
         * message from client to server requesting score
         */
        val SCORE = "SCORE"

        /**
         * PASSED color
         *
         * message from the server to the client that the color client passed
         */
        val PASSED = "PASSED"

        /**
         * CHAT message
         *
         * String message from the server to the client, or from the client to the server
         */
        val CHAT = "CHAT"

        /**
         * WARNING message
         *
         * String message from the server to the client, only send when a splitMessage is incorrect
         */
        val WARNING = "WARNING"

        /**
         * READY color opponent boardsize
         *
         * sends ready right before the game with own color, String color, with opponent name, String opponent, and with the board size, int boardsize
         */
        val READY = "READY"

        /**
         * VALID color x y
         *
         * server notifies the client of a valid move with the color of the move and the position, x and y
         */
        val VALID = "VALID"

        /**
         * INVALID color message
         *
         * server notifies the client of an invalid move with the color of the invalid move and a String message
         */
        val INVALID = "INVALID"

        /**
         * END score1 score2
         *
         * server notifies both clients with the scores.
         */
        val END = "END"

        /**
         * TABLEFLIP
         *
         * client notifies the server that it surrenders
         */
        val TABLEFLIP = "TABLEFLIP"

        /**
         * PASS
         *
         * client notifies the server that it passes
         */
        val PASS = "PASS"

        /**
         * EXIT
         *
         * client notifies the server that it will exit the server
         */
        val EXIT = "EXIT"

        /**
         * CANCEL
         *
         * When waiting, the client can cancel
         */
        val CANCEL = "CANCEL"

        /**
         * MOVE x y
         *
         * client notifies the server that it will move his stone to x or y
         */
        val MOVE = "MOVE"

        /**
         * PLAYER name
         *
         * client sends desired name (String) to the server
         */
        val PLAYER = "PLAYER"

        /**
         * GO dim
         *
         * client sends desired dim (int) to the server
         */
        val GO = "GO"

        /**
         * delimiter to split the commands
         */
        val DELIMITER = " "
    }
}


