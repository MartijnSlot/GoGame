package com.nedap.university.go.model

/**
 * Represents a Stone in a GO game. Three possible values:
 * Stone.BLACK, Stone.WHITE and Stone.EMPTY.
 *
 * @author Martijn Slot
 * @version 1.0
 */
enum class Stone {

    EMPTY, BLACK, WHITE;


    fun toString(): String {
        if (this == BLACK) {
            return "black"
        } else if (this == WHITE) {
            return "white"
        }
        return "."
    }

    fun toTUIString(): String {
        if (this == BLACK) {
            return "B"
        } else if (this == WHITE) {
            return "W"
        }
        return "."
    }
}
