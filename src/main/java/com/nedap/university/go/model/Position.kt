package com.nedap.university.go.model

import java.util.Objects

/**
 * Class for creating a position on a GO board.
 *
 * @author Martijn Slot
 * @version 1.0
 */
class Position(val x: Int, val y: Int) {

    @Override
    fun equals(o: Object): Boolean {
        if (o !is Position) return false
        return if (o === this) true else (o as Position).x == this.x && (o as Position).y == this.y
    }

    @Override
    fun hashCode(): Int {
        return Objects.hash(x, y)
    }

}

