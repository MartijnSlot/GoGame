package com.nedap.university.go.viewer

/**
 * Created by daan.vanbeek on 13-12-16.
 */
interface GOGUI {

    /**
     * Sets the board width and height to the given value. Adjusts the window size accordingly.
     * Re-initialises the board, note that the existing stone configuration will be lost.
     * @param size the desired width and height of the board.
     * @throws InvalidCoordinateException when x or y coordinate fall outside of the board.
     */
    @Throws(InvalidCoordinateException::class)
    fun setBoardSize(size: Int)

    /**
     * Adds a new stone to the board of the given type and at the given position.
     * Removes any existing stone/area indicator at the given position.
     * @param x the x coordinate of the new stone, ranges from 0 to boardSize - 1.
     * @param y the y coordinate of the new stone, ranges from 0 to boardSize - 1.
     * @param white if true then a white stone will be added, otherwise a black stone will be added
     * @throws InvalidCoordinateException when x or y coordinate fall outside of the board.
     */
    @Throws(InvalidCoordinateException::class)
    fun addStone(x: Int, y: Int, white: Boolean)

    /**
     * Removes any existing stone at the given position.
     * Does nothing if the position currently has no stone/area indicator.
     * @param x the x coordinate of the stone/area indicator to remove, ranges from 0 to boardSize - 1.
     * @param y the y coordinate of the stone/area indicator to remove, ranges from 0 to boardSize - 1.
     * @throws InvalidCoordinateException when x or y coordinate fall outside of the board.
     */
    @Throws(InvalidCoordinateException::class)
    fun removeStone(x: Int, y: Int)

    /**
     * Adds a new area indicator to the board of the given type and at the given position.
     * Removes any existing stone/area indicator at the given position.
     * @param x the x coordinate of the new stone, ranges from 0 to boardSize - 1.
     * @param y the y coordinate of the new stone, ranges from 0 to boardSize - 1.
     * @param white if true then a white stone will be added, otherwise a black stone will be added
     * @throws InvalidCoordinateException when x or y coordinate fall outside of the board.
     */
    @Throws(InvalidCoordinateException::class)
    fun addAreaIndicator(x: Int, y: Int, white: Boolean)

    /**
     * Adds a hint indicator to the board of the given type and at the given position.
     * @param x the x coordinate of the hint, ranges from 0 to boardSize - 1.
     * @param y the y coordinate of the hint, ranges from 0 to boardSize - 1.
     * @throws InvalidCoordinateException when x or y coordinate fall outside of the board.
     */
    @Throws(InvalidCoordinateException::class)
    fun addHintIndicator(x: Int, y: Int)

    /**
     * Hides the hint indicator.
     */
    fun removeHintIdicator()

    /**
     * Clears the board of all stones.
     */
    fun clearBoard()

    /**
     * Starts the GO graphical user interface
     */
    fun startGUI()

    /**
     * Stops the GO graphical user interface
     */
    fun stopGUI()

}