package com.nedap.university.go.viewer

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Sphere
import javafx.stage.Stage

import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class GOGUIImpl : Application() {

    private var currentBoardWidth = INITIAL_BOARD_SIZE
    private var currentBoardHeight = INITIAL_BOARD_SIZE
    private val currentSquareSize = INITIAL_SQUARE_SIZE

    private var board: Array<Array<Node>>? = null
    private val boardLines = ArrayList()
    private var root: Group? = null
    private var primaryStage: Stage? = null
    private var hint: Node? = null

    private var mode3D = true
    private var showStartupAnimation = false

    private val blackMaterial = PhongMaterial()
    private val whiteMaterial = PhongMaterial()
    private val yellowMaterial = PhongMaterial()

    protected fun countDownConfigurationLatch() {
        waitForConfigurationLatch.countDown()
    }

    protected fun setShowStartupAnimation(showStartupAnimation: Boolean) {
        this.showStartupAnimation = showStartupAnimation
    }

    protected fun setMode3D(mode3D: Boolean) {
        this.mode3D = mode3D
    }

    @Override
    fun start(primaryStage: Stage) {
        instance = this
        initDrawMaterials()

        try {
            waitForConfigurationLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        this.primaryStage = primaryStage

        primaryStage.setTitle("GO")

        initNewBoard()

        if (showStartupAnimation) {
            runStartupAnimation()
        } else {
            initializationLatch.countDown()
        }
    }

    private fun initDrawMaterials() {
        blackMaterial.setDiffuseColor(Color.BLACK)
        blackMaterial.setSpecularColor(Color.LIGHTBLUE)
        whiteMaterial.setDiffuseColor(Color.WHITE)
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE)
        yellowMaterial.setDiffuseColor(Color.YELLOW)
        yellowMaterial.setSpecularColor(Color.LIGHTBLUE)
    }

    private fun runStartupAnimation() {
        val startNanoTime = System.nanoTime()

        val animationTimer = object : AnimationTimer() {
            internal var roundCount = 0
            internal var lastX = 0

            fun handle(currentNanoTime: Long) {
                val t = (currentNanoTime - startNanoTime) / 50000000.0

                val x = (t % currentBoardWidth).toInt()

                if (x < lastX) {
                    roundCount++
                }

                if (x != lastX) {
                    if (roundCount >= 2) {
                        stop()
                        clearBoard()
                        initializationLatch.countDown()
                    } else {
                        clearBoard()
                        if (x % 2 != 0) {
                            drawDiagonalStoneLine(x - 1, false, roundCount != 0)
                            drawDiagonalStoneLine(x, true, roundCount != 0)
                            drawDiagonalStoneLine(x + 1, false, roundCount != 0)
                        } else {
                            drawDiagonalStoneLine(x - 1, true, roundCount != 0)
                            drawDiagonalStoneLine(x, false, roundCount != 0)
                            drawDiagonalStoneLine(x + 1, true, roundCount != 0)
                        }
                    }

                    lastX = x
                }
            }
        }
        animationTimer.start()
    }

    private fun initNewBoard() {
        root = Group()
        board = Array<Array<Node>>(currentBoardWidth) { arrayOfNulls<Node>(currentBoardHeight) }

        val scene = Scene(root, (currentBoardWidth + 1) * currentSquareSize, (currentBoardHeight + 1) * currentSquareSize)
        primaryStage!!.setScene(scene)
        primaryStage!!.show()

        val pattern = ImagePattern(Image("background_1920.jpg"))
        scene.setFill(pattern)

        initBoardLines()
    }

    private fun initBoardLines() {
        root!!.getChildren().removeAll(boardLines)
        boardLines.clear()

        val height = currentBoardHeight
        val width = currentBoardWidth
        val squareSize = currentSquareSize

        // Draw horizontal lines
        for (i in 1..height) {
            boardLines.add(Line(squareSize, i * squareSize, width * squareSize, i * squareSize))
        }

        // Draw vertical lines
        for (i in 1..width) {
            boardLines.add(Line(i * squareSize, squareSize, i * squareSize, height * squareSize))
        }

        root!!.getChildren().addAll(boardLines)

        if (mode3D) {
            hint = Sphere(currentSquareSize / 2)
            (hint as Sphere).setMaterial(yellowMaterial)
        } else {
            hint = Circle(currentSquareSize / 2)
            (hint as Circle).setFill(Color.YELLOW)
        }
        hint!!.setVisible(false)
        root!!.getChildren().add(hint)
    }

    private fun drawDiagonalStoneLine(diagonal: Int, stoneType: Boolean, flip: Boolean) {
        try {
            for (x in 0 until currentBoardWidth) {
                for (y in 0 until currentBoardHeight) {
                    if (x + y == diagonal * 2) {
                        if (!flip) {
                            addStone(x, y, stoneType)
                        } else {
                            addStone(currentBoardWidth - 1 - x, y, stoneType)
                        }
                    }
                }
            }
        } catch (e: InvalidCoordinateException) {
            throw IllegalStateException(e)
        }

    }

    @Throws(InvalidCoordinateException::class)
    protected fun addStone(x: Int, y: Int, white: Boolean) {
        checkCoordinates(x, y)
        removeStone(x, y)

        if (mode3D) {
            val newStone = Sphere(currentSquareSize / 2)

            if (white) {
                newStone.setMaterial(whiteMaterial)
            } else {
                newStone.setMaterial(blackMaterial)
            }

            newStone.setTranslateX((x + 1) * currentSquareSize)
            newStone.setTranslateY((y + 1) * currentSquareSize)
            board!![x][y] = newStone

            root!!.getChildren().add(newStone)
        } else {
            val newStone = Circle((x + 1) * currentSquareSize, (y + 1) * currentSquareSize, currentSquareSize / 2)

            if (white) {
                newStone.setFill(Color.WHITE)
            } else {
                newStone.setFill(Color.BLACK)
            }

            board!![x][y] = newStone
            root!!.getChildren().add(newStone)
        }
    }

    @Throws(InvalidCoordinateException::class)
    protected fun removeStone(x: Int, y: Int) {
        checkCoordinates(x, y)

        if (board!![x][y] != null) {
            root!!.getChildren().remove(board!![x][y])
        }
        board!![x][y] = null
    }

    @Throws(InvalidCoordinateException::class)
    protected fun addAreaIndicator(x: Int, y: Int, white: Boolean) {
        checkCoordinates(x, y)
        removeStone(x, y)

        if (mode3D) {
            val areaStone = Box(currentSquareSize / 3, currentSquareSize / 3, currentSquareSize / 3)
            areaStone.setMaterial(if (white) whiteMaterial else blackMaterial)
            areaStone.setTranslateX((x + 1) * currentSquareSize)
            areaStone.setTranslateY((y + 1) * currentSquareSize)
            board!![x][y] = areaStone
            root!!.getChildren().add(areaStone)
        } else {
            val areaStone = Rectangle(
                (x + 1) * currentSquareSize - currentSquareSize / 6,
                (y + 1) * currentSquareSize - currentSquareSize / 6,
                currentSquareSize / 3,
                currentSquareSize / 3
            )
            areaStone.setFill(if (white) Color.WHITE else Color.BLACK)
            board!![x][y] = areaStone
            root!!.getChildren().add(areaStone)
        }
    }

    @Throws(InvalidCoordinateException::class)
    protected fun addHintIndicator(x: Int, y: Int) {
        hint!!.setTranslateX((x + 1) * currentSquareSize)
        hint!!.setTranslateY((y + 1) * currentSquareSize)
        hint!!.setVisible(true)
    }

    protected fun removeHintIdicator() {
        hint!!.setVisible(false)
    }

    @Throws(InvalidCoordinateException::class)
    private fun checkCoordinates(x: Int, y: Int) {
        if (x < 0 || x >= currentBoardWidth) {
            throw InvalidCoordinateException("x coordinate is outside of board range. x coordinate: " + x + " board range: 0-" + (currentBoardWidth - 1))
        }

        if (y < 0 || y >= currentBoardHeight) {
            throw InvalidCoordinateException("y coordinate is outside of board range. y coordinate: " + y + " board range: 0-" + (currentBoardHeight - 1))
        }
    }

    protected fun clearBoard() {
        try {
            for (x in 0 until currentBoardWidth) {
                for (y in 0 until currentBoardHeight) {
                    removeStone(x, y)
                }
            }
        } catch (e: InvalidCoordinateException) {
            throw IllegalStateException(e)
        }

    }

    protected fun setBoardSize(size: Int) {
        currentBoardHeight = size
        currentBoardWidth = size

        initNewBoard()
    }

    protected fun setInitialBoardSize(size: Int) {
        currentBoardHeight = size
        currentBoardWidth = size
    }

    protected fun waitForInitializationLatch() {
        try {
            System.out.println("Attempting init of the GOGUI!")
            if (!initializationLatch.await(30, TimeUnit.SECONDS)) {
                System.out.println("Initialization of the GOGUI failed!")
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    companion object {

        private val INITIAL_BOARD_SIZE = 19
        private val INITIAL_SQUARE_SIZE = 50

        private val waitForConfigurationLatch = CountDownLatch(1)
        private val initializationLatch = CountDownLatch(1)

        var instance: GOGUIImpl? = null
            private set

        protected val isInstanceAvailable: Boolean
            get() = instance != null

        protected fun startGUI() {
            object : Thread() {
                fun run() {
                    Application.launch(GOGUIImpl::class.java)
                }
            }.start()
        }
    }
}// Has to be public otherwise JavaFX cannot find it
