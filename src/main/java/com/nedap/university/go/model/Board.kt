package com.nedap.university.go.model

import java.util.HashMap
import java.util.HashSet

/**
 * Represents a board in a Go game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
class Board
/**
 * constructor of board of size dim * dim, containing only EMPTY fields.
 * @param dim
 */
(val dim: Int) {
    val points: Map<Position, Point> = HashMap()
    var blackScore = 0
        private set
    var whiteScore = 0
        private set

    init {
        for (x in 0 until dim) {
            for (y in 0 until dim) {
                this.points.put(Position(x, y), Point(Stone.EMPTY))
            }
        }
    }

    fun isPoint(pos: Position): Boolean {
        return this.points.containsKey(pos)
    }

    private fun getPoint(pos: Position): Point {
        return points[pos]
    }

    fun isEmptyPoint(pos: Position): Boolean {
        return this.getPoint(pos).getStone() === Stone.EMPTY
    }

    fun setPoint(pos: Position, s: Stone) {
        points.put(pos, Point(s))
    }

    /**
     * Empties a position / removes a stone.
     * @param pos
     */
    fun removePoint(pos: Position) {
        points.put(pos, Point(Stone.EMPTY))
    }

    /**
     * Returns a set of 2 (corner), 3 (edge) or 4 (center) neighbours of a specific position
     * @param pos
     * @return set
     */
    private fun getNeighbours(pos: Position): Set<Position> {
        val neighbours = HashSet()

        for (i in pos.getX() - 1..pos.getX() + 1) {
            val a = Position(i, pos.getY())
            if (isPoint(a)) neighbours.add(a)
        }
        for (i in pos.getY() - 1..pos.getY() + 1) {
            val a = Position(pos.getX(), i)
            if (isPoint(a)) neighbours.add(a)
        }
        return neighbours
    }


    /**
     * Returns all empty positions surrounding argument position.
     * @param cluster
     * @param stone
     * @return set
     */
    private fun encapsulatedBy(cluster: Set<Position>, stone: Stone): Set<Position> {
        val freePositions = HashSet()

        for (clusterPos in cluster) {
            for (p in getNeighbours(clusterPos)) {
                if (getPoint(p).getStone() === stone) {
                    freePositions.add(p)
                }
            }
        }
        return freePositions
    }

    /**
     * Returns a cluster of defending stone positions in which position pos is situated.
     * @param pos
     * @return set
     */
    fun defendingCluster(pos: Position): Set<Position> {
        val defend = getPoint(pos).getStone()
        val defendingCluster = HashSet()
        val temp = HashSet()

        defendingCluster.add(pos)

        while (temp.size() !== defendingCluster.size()) {
            for (r in defendingCluster) temp.add(r)
            for (p in temp) {
                for (q in getNeighbours(p)) {
                    if (isPoint(q) && getPoint(q).getStone() === defend) {
                        defendingCluster.add(q)
                    }
                }
            }
        }
        return defendingCluster
    }


    /**
     * Returns all liberty positions surrounding argument position (even if arguments exists in cluster of samecolor stones).
     * @param pos
     * @return set
     */
    private fun surroundingStones(pos: Position, stone: Stone): Set<Position> {
        return encapsulatedBy(defendingCluster(pos), stone)
    }

    /**
     * Returns number of liberties of argument (even if arguments exists in cluster of samecolor stones).
     * @param pos
     * @return int
     */
    fun numberOfLiberties(pos: Position): Int {
        return surroundingStones(pos, Stone.EMPTY).size()
    }


    /**
     * checks if the placement of a stone on pos is legal
     * stone is placed outside of the dimensions of the board
     * stone is placed on an occupied spot (black, white)
     * @param x
     * @oaran y
     * @return boolean
     */
    fun isAllowed(x: Int, y: Int): Boolean {
        if (isPoint(Position(x, y)) && isEmptyPoint(Position(x, y))) {
            return true
        }
        System.out.println("Move not allowed: position does not exist on this playing board, or the position is not Empty.")
        return false
    }


    /**
     * counts the endscore on the board.
     * int[0] = score Stone.BLACK
     * int[1] = score Stone.WHITE
     * territory count has been removed because it caused errors.
     */
    fun countScore() {
        val whiteTerritory = HashSet()
        val blackTerritory = HashSet()

        for (p in points.keySet()) {
            when (points[p].getStone()) {
                EMPTY -> {

                    val blackEnc = encapsulatedBy(defendingCluster(p), Stone.BLACK).size()
                    val whiteEnc = encapsulatedBy(defendingCluster(p), Stone.WHITE).size()

                    if (blackEnc > 0 && whiteEnc == 0) {
                        blackTerritory.add(p)
                    }

                    if (whiteEnc > 0 && blackEnc == 0) {
                        whiteTerritory.add(p)
                    }
                }
                BLACK -> blackTerritory.add(p)
                WHITE -> whiteTerritory.add(p)
            }
        }

        blackScore = blackTerritory.size()
        whiteScore = whiteTerritory.size()
    }

    fun countScoreTableflip() {
        blackScore = -1
        whiteScore = -1
    }

    /**
     * prints a string that used for creating board history
     * @return String
     */
    fun toSimpleString(): String {
        var boardString = ""
        for (i in 0 until dim) {
            for (j in 0 until dim)
                boardString = boardString + getPoint(Position(i, j)).getStone().toTUIString()
        }
        return boardString
    }

    /**
     * Prints a GTUI
     * @return String
     */
    fun toString(): String {
        var s = "  "
        for (i in 0 until dim) {
            s = "$s$i "
        }
        s = s + "\n"
        for (i in 0 until dim) {
            var row = "" + i
            for (j in 0 until dim) {
                row = row + " " + getPoint(Position(i, j)).getStone().toTUIString()
            }
            s = s + row
            if (i < dim) {
                s = s + "\n"
            }
        }
        return s
    }

}
