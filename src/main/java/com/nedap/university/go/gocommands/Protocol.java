package com.nedap.university.go.gocommands;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public interface Protocol {

    /**
     * TABLEFLIPPED color
     *
     * message from the server to the client that the color client surrendered
     */
    String TABLEFLIPPED = "TABLEFLIPPED";

    /**
     * PASSED color
     *
     * message from the server to the client that the color client passed
     */
    String PASSED = "PASSED";

    /**
     * CHAT message
     *
     * String message from the server to the client, or from the client to the server
     */
    String CHAT = "CHAT";

    /**
     * WARNING message
     *
     * String message from the server to the client, only send when a splitMessage is incorrect
     */
    String WARNING = "WARNING";

    /**
     * READY color opponent boardsize
     *
     * sends ready right before the game with own color, String color, with opponent name, String opponent, and with the board size, int boardsize
     */
    String READY = "READY";

    /**
     * VALID color x y
     *
     * server notifies the client of a valid move with the color of the move and the position, x and y
     */
    String VALID = "VALID";

    /**
     * INVALID color message
     *
     * server notifies the client of an invalid move with the color of the invalid move and a String message
     */
    String INVALID = "INVALID";

    /**
     * END score1 score2
     *
     * server notifies both clients with the scores.
     */
    String END = "END";

    /**
     * TABLEFLIP
     *
     * client notifies the server that it surrenders
     */
    String TABLEFLIP = "TABLEFLIP";

    /**
     * PASS
     *
     * client notifies the server that it passes
     */
    String PASS = "PASS";

    /**
     * EXIT
     *
     * client notifies the server that it will exit the server
     */
    String EXIT = "EXIT";

    /**
     * CANCEL
     *
     * When waiting, the client can cancel
     */
    String CANCEL = "CANCEL";

    /**
     * MOVE x y
     *
     * client notifies the server that it will move his stone to x or y
     */
    String MOVE = "MOVE";

    /**
     * PLAYER name
     *
     * client sends desired name (String) to the server
     */
    String PLAYER = "PLAYER";

    /**
     * GO dim
     *
     * client sends desired dim (int) to the server
     */
    String GO = "GO";

    /**
     * delimiter to split the commands
     */
    String DELIMITER = " ";
}


