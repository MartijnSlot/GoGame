package com.nedap.university.go.client

import com.nedap.university.go.aiStrategies.AiBassie
import com.nedap.university.go.aiStrategies.AiKasparov
import com.nedap.university.go.aiStrategies.Strategy
import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.gocommands.DetermineCommand

import java.io.*
import java.net.*

/**
 * Class for creating a GO client.
 *
 * @author Martijn Slot
 * @version 1.0
 */

class GoClient @Throws(IOException::class)
constructor(serverAddress: String, serverPort: Int) : Thread() {

    internal var socket: Socket? = null
        private set
    private var inputFromPlayer: BufferedReader? = null
    private var serverHandler: ServerHandler? = null
    private var ai = false
    private var inputFromComputerPlayer: String? = null
    private var aiPlayer: Strategy? = null


    init {
        System.out.println("Client connecting to port $serverPort\n Server IP: $serverAddress")
        try {
            socket = Socket(serverAddress, serverPort)
            inputFromPlayer = BufferedReader(InputStreamReader(System.`in`))
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Override
    fun run() {
        serverHandler = ServerHandler(this, socket)
        serverHandler!!.start()
        while (serverHandler!!.getSocket().isConnected() && serverHandler!!.getSocket() != null) {
            if (inputFromPlayer != null) {
                runHuman()
            }
            if (ai) {
                runAI()

            }
        }
    }

    /**
     * whenever an AI is involved, this method is run, it makes a distinction between client stati.
     *
     */
    private fun runAI() {
        when (serverHandler!!.getClientStatus()) {
            INGAME_TURN -> {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                inputFromComputerPlayer = aiPlayer!!.determineMove(serverHandler!!.getGame())
                val determineCommand = DetermineCommand()
                val command = determineCommand.inputCommand(inputFromComputerPlayer, this)
                command.execute()
            }
            INGAME_NOT_TURN, WAITING, PREGAME -> try {
                synchronized(this) {
                    this.wait()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * whenever an Human player is involved, this method is run.
     *
     */
    private fun runHuman() {
        val fromPlayer: String
        try {
            fromPlayer = inputFromPlayer!!.readLine()
            val determineCommand = DetermineCommand()
            val command = determineCommand.inputCommand(fromPlayer, this)
            command.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun handleMoveFromPlayer(x: Int, y: Int) {
        serverHandler!!.checkAndSendPlayerMove(x, y)
    }

    fun handleAnythingFromPlayerExceptMoveExitGoChatAndPlayer(splitMessage: Array<String>) {
        serverHandler!!.sendPlayerCommand(splitMessage)
    }


    fun handleChatCommandFromPlayer(splitMessage: Array<String>) {
        serverHandler!!.sendPlayerCommand(splitMessage)
    }

    fun handleExitFromPlayer(splitMessage: Array<String>) {
        serverHandler!!.sendPlayerCommand(splitMessage)
        shutdown()
    }

    fun handlePlayerCommandFromPlayer(splitMessage: Array<String>) {
        serverHandler!!.sendPlayerCommand(splitMessage)
        serverHandler!!.setClientName(splitMessage[1])
    }

    fun handleGoFromPlayer(splitMessage: Array<String>) {
        if (serverHandler!!.getClientName() != null) {
            serverHandler!!.sendPlayerCommand(splitMessage)
            when (serverHandler!!.getClientName()) {
                "clownbassie" -> {
                    System.out.println("Asjemenou, Clown Bassie komt je pakken! ")
                    ai = true
                    aiPlayer = AiBassie()
                    inputFromPlayer = null
                }
                "garrykasparov" -> {
                    System.out.println("Kasparov: goeie schaker, slechte GO-er. ")
                    ai = true
                    aiPlayer = AiKasparov()
                    inputFromComputerPlayer = aiPlayer!!.determineMove(serverHandler!!.getGame())
                    inputFromPlayer = null
                }
                else -> {
                }
            }
        } else {
            System.out.println("You have no name, enter PLAYER name first. ")
        }
    }

    /**
     * shuts down the client.
     *
     */
    internal fun shutdown() {
        try {
            System.out.println("Exited from server. Disconnection just happened.")
            inputFromPlayer!!.close()
            socket!!.close()
        } catch (e: IOException) {
            System.out.println("Some things were already closed, but we close it anyway. For your convenience.")
        }

        System.exit(0)
    }

}

