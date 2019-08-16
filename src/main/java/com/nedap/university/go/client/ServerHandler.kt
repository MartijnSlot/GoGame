package com.nedap.university.go.client

import com.nedap.university.go.controller.Game
import com.nedap.university.go.gocommands.*
import com.nedap.university.go.model.Position
import com.nedap.university.go.viewer.GoGUIIntegrator
import javafx.application.Platform

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

/**
 * Class for creating a Server.
 * handles all communication between client and ClientHandler
 *
 * @author Martijn Slot
 * @version 1.0
 */
class ServerHandler(private val client: GoClient, internal val socket: Socket?) : Thread() {
    private var inputFromServer: BufferedReader? = null
    private var outputToServer: BufferedWriter? = null
    internal var clientName: String? = null
    var game: Game? = null
        private set
    private val gogui: GoGUIIntegrator
    private var color: String? = null
    var clientStatus = ClientStatus.PREGAME
        private set


    init {
        this.gogui = GoGUIIntegrator(false, true, 1)
        try {
            inputFromServer = BufferedReader(InputStreamReader(client.getSocket().getInputStream()))
            outputToServer = BufferedWriter(OutputStreamWriter(client.getSocket().getOutputStream()))
        } catch (ioe: IOException) {
            ioe.getStackTrace()

        }

    }

    @Override
    fun run() {
        try {
            while (socket != null && socket!!.isConnected()) {
                val fromServer = inputFromServer!!.readLine()
                if (fromServer != null) {
                    val determineCommand = DetermineCommand()
                    val command = determineCommand.determineClientCommand(fromServer, this)
                    command.execute()
                }

            }
            shutdown()
        } catch (e: IOException) {
            System.out.println("No input")
        }

    }

    private fun writeToServer(message: String) {
        try {
            outputToServer!!.write(message)
            outputToServer!!.newLine()
            outputToServer!!.flush()
        } catch (e: IOException) {
            System.out.println("You played on a crappy server. Server has died, disconnection just happened.")
            shutdown()
        }

    }

    private fun shutdown() {
        try {
            outputToServer!!.close()
            inputFromServer!!.close()
            Platform.exit()
            client.shutdown()
        } catch (e: IOException) {
            System.out.println("Trying to shutdown. Everything is GOne. ")
        }

    }

    private fun sewString(splitMessage: Array<String>): String {
        return String.join(Protocol.DELIMITER, splitMessage)
    }


    private fun switchTurns() {
        if (clientStatus === ClientStatus.INGAME_NOT_TURN) {
            clientStatus = ClientStatus.INGAME_TURN
            synchronized(client) {
                client.notify()
            }
            System.out.println("Your turn, $color $clientName")
        } else {
            clientStatus = ClientStatus.INGAME_NOT_TURN
            System.out.println("NOT your turn, $color $clientName")
        }
    }

    /**
     * checks what to do when the initial splitMessage is given by the GoClient
     *
     * @param splitMessage that contains the READY command
     */
    fun handleReady(splitMessage: Array<String>) {
        val boardSize = Integer.parseInt(splitMessage[3])
        color = splitMessage[1]
        game = Game(boardSize)
        gogui.setBoardSize(boardSize)
        gogui.startGUI()
        clientStatus = if (color!!.equals("white")) ClientStatus.INGAME_NOT_TURN else ClientStatus.INGAME_TURN
        synchronized(client) {
            client.notify()
        }
        System.out.println("New game started on a board with dimension " + splitMessage[3] + " \nYour stone: " + splitMessage[1] + "\nYour opponent: " + splitMessage[2])
        if (color!!.equals("black")) {
            System.out.println("\n\nYou can start, young padawan.")
        }
    }

    /**
     * checks the input of 'VALID' given by the server and acts on it locally
     *
     */
    fun handleValid(splitMessage: Array<String>) {
        val white = splitMessage[1].equals("white")
        val x = Integer.parseInt(splitMessage[2])
        val y = Integer.parseInt(splitMessage[3])
        game!!.doMove(x, y)
        gogui.addStone(x, y, white)
        for (a in game!!.autoRemoveSet) {
            gogui.removeStone(a.getX(), a.getY())
        }
        game!!.autoRemoveSet.clear()
        switchTurns()
    }

    fun handleInvalid(splitMessage: Array<String>) {
        System.out.println("Game has ended due to INVALID move of player " + splitMessage[1] + "\n" + splitMessage[2])
        if (splitMessage[1].equals(color)) {
            shutdown()
        } else {
            clearGameSetPregame()
        }
    }

    fun handlePassed(splitMessage: Array<String>) {
        game!!.passMove()
        System.out.println("Player " + splitMessage[1] + " has passed.")
        if (!clientStatus.equals(ClientStatus.PREGAME)) {
            switchTurns()
        }
    }

    fun handleEnd(splitMessage: Array<String>) {
        val scoreBlack = Integer.parseInt(splitMessage[1])
        val scoreWhite = Integer.parseInt(splitMessage[2])
        System.out.println("Game has ended. Score black: $scoreBlack\nScore white: $scoreWhite")
        clearGameSetPregame()
    }

    fun handleIncomingChat(splitMessage: Array<String>) {
        val chat = sewString(splitMessage)
        System.out.println(chat)
    }

    fun handleTableFlipped(splitMessage: Array<String>) {
        val chat = sewString(splitMessage)
        System.out.println(chat)
        clearGameSetPregame()
    }

    fun handleWarning(splitMessage: Array<String>) {
        val chat = sewString(splitMessage)
        System.out.println(chat)
    }

    internal fun checkAndSendPlayerMove(x: Int, y: Int) {
        if (game != null && game!!.moveAllowed(x, y)) {
            writeToServer("MOVE $x $y")
        } else {
            System.out.println("Illegal move, do not send this move to the server. Try again.")
        }
    }

    internal fun sendPlayerCommand(splitMessage: Array<String>) {
        val commandToSend = sewString(splitMessage)
        writeToServer(commandToSend)
    }

    private fun clearGameSetPregame() {
        clientStatus = ClientStatus.PREGAME
        System.out.println("ClientStatus: " + ClientStatus.PREGAME + " Please enter your GO dim.")
        try {
            inputFromServer = BufferedReader(InputStreamReader(client.getSocket().getInputStream()))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        game!!.reset()
        gogui.clearBoard()
        run()
    }


}
