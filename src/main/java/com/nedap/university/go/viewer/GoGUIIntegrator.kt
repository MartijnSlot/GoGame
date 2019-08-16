package com.nedap.university.go.viewer

import javafx.application.Platform

/**
 * Created by daan.vanbeek on 13-12-16.
 */
class GoGUIIntegrator
/**
 * Creates a GoGUIIntegrator that is capable of configuring and controlling the GO GUI.
 * @param showStartupAnimation if true then a startup animation will be shown when the GO GUI is started.
 * @param mode3D if true then the stones will be shown in 3D. Otherwise a 2D representation will be used.
 * @param boardSize the desired initial board size.
 */
(showStartupAnimation: Boolean, mode3D: Boolean, boardSize: Int) : GOGUI {

    private var wrappee: GOGUIImpl? = null

    init {
        createWrappedObject()
        wrappee!!.setShowStartupAnimation(showStartupAnimation)
        wrappee!!.setMode3D(mode3D)
        wrappee!!.setInitialBoardSize(boardSize)
    }

    @Override
    @Synchronized
    fun setBoardSize(size: Int) {
        Platform.runLater({ wrappee!!.setBoardSize(size) })
    }

    @Override
    @Synchronized
    fun addStone(x: Int, y: Int, white: Boolean) {
        Platform.runLater({
            try {
                wrappee!!.addStone(x, y, white)
            } catch (e: InvalidCoordinateException) {
                e.printStackTrace()
            }
        })
    }

    @Override
    @Synchronized
    fun removeStone(x: Int, y: Int) {
        Platform.runLater({
            try {
                wrappee!!.removeStone(x, y)
            } catch (e: InvalidCoordinateException) {
                e.printStackTrace()
            }
        })
    }

    @Override
    @Synchronized
    fun addAreaIndicator(x: Int, y: Int, white: Boolean) {
        Platform.runLater({
            try {
                wrappee!!.addAreaIndicator(x, y, white)
            } catch (e: InvalidCoordinateException) {
                e.printStackTrace()
            }
        })
    }

    @Override
    @Synchronized
    fun addHintIndicator(x: Int, y: Int) {
        Platform.runLater({
            try {
                wrappee!!.addHintIndicator(x, y)
            } catch (e: InvalidCoordinateException) {
                e.printStackTrace()
            }
        })
    }

    @Override
    @Synchronized
    fun removeHintIdicator() {
        Platform.runLater({ wrappee!!.removeHintIdicator() })
    }

    @Override
    @Synchronized
    fun clearBoard() {
        Platform.runLater({ wrappee!!.clearBoard() })
    }

    @Override
    @Synchronized
    fun startGUI() {
        startJavaFX()
        wrappee!!.waitForInitializationLatch()
        System.out.println("GO GUI was successfully started!")
    }

    @Override
    @Synchronized
    fun stopGUI() {
        // Not implemented yet
    }

    private fun createWrappedObject() {
        if (wrappee == null) {
            GOGUIImpl.startGUI()

            while (!GOGUIImpl.isInstanceAvailable()) {
                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }

            wrappee = GOGUIImpl.getInstance()
        }
    }

    private fun startJavaFX() {
        createWrappedObject()
        wrappee!!.countDownConfigurationLatch()
    }
}
