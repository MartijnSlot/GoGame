package com.nedap.university.go.server

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.gocommands.DetermineCommand
import com.nedap.university.go.gocommands.Protocol

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.UUID

/**
 * Class for creating a ClientHandler.
 * handles all communication between server and ServerHandler
 *
 * @author Martijn Slot
 * @version 1.0
 */

class ClientHandler
/**
 * threaded clienthandler constructor
 *
 * @param socket socket
 * @param server GoServer
 */
(private val socket: Socket?, private val server: GoServer) : Thread() {
    private var singleGameServer: SingleGameServer? = null
    private var outputToClient: BufferedWriter? = null
    private var inputFromClient: BufferedReader? = null
    var clientStatus: ClientStatus? = null
        internal set
    internal var dim: Int = 0
        private set
    var clientName: String? = null
        private set
    internal var color: String? = null
    private val uuid = UUID.randomUUID().toString()

    init {
        this.clientStatus = ClientStatus.PREGAME
        try {
            inputFromClient = BufferedReader(InputStreamReader(socket.getInputStream()))
            outputToClient = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * pre run-function for client commands
     */
    @Override
    fun run() {

        try {
            while (socket != null && socket!!.isConnected()) {
                val fromClient = inputFromClient!!.readLine()
                if (fromClient != null) {
                    System.out.println("uuid: " + this.uuid + "\nreceived: " + fromClient)
                    val determineCommand = DetermineCommand()
                    val command = determineCommand.determineServerCommand(fromClient, this)
                    command.execute()
                }
            }
        } catch (e: IOException) {
            System.out.println("Your stream is already kloost.")
        }

        annihilatePlayer()
    }

    /**
     * kicks a player from the server for making an illegal move
     *
     */
    internal fun annihilatePlayer() {
        try {
            outputToClient!!.close()
            inputFromClient!!.close()
            socket!!.close()
        } catch (e: IOException) {
            System.out.println("Player annihilated due to a wrong move!")
        }

        server.eraseClient(this)
    }

    /**
     * sews a splitted string back together
     * @param splitMessage list of strings
     * @return string
     */
    private fun sewString(splitMessage: Array<String>): String {
        return String.join(Protocol.DELIMITER, splitMessage)
    }

    /**
     * general message writer from server to client
     *
     * @param message string
     */
    fun writeToClient(message: String) {
        try {
            outputToClient!!.write(message)
            outputToClient!!.newLine()
            outputToClient!!.flush()
        } catch (e: IOException) {
            e.getStackTrace()
        }

    }

    /**
     * setter for singlegameserver
     *
     * @param singleGameServer singlegameserver
     */
    internal fun setSingleGameServer(singleGameServer: SingleGameServer) {
        this.singleGameServer = singleGameServer
    }

    /**
     * make sure the name of this clientHandler is set
     * enters this clienthandler into the list of clients on the server
     *
     * @param splitMessage)  which is a split String list of the entry by the player
     */
    fun handlePlayerCommand(splitMessage: Array<String>) {
        clientName = splitMessage[1]
        writeToClient("CHAT server - Great success! You have entered your name: " + clientName!!)
    }

    fun handleCancelCommand() {
        this.clientStatus = ClientStatus.PREGAME
        server.statusWaitingToInitial(this)
        writeToClient("CHAT server - Great success! You have set your status to PREGAME. Please enter GO boardsize, " + clientName!!)
    }

    fun chatToAll(splitMessage: Array<String>) {
        val message = sewString(splitMessage)
        server.chatToAllPlayers("CHAT all via server $clientName: $message")
    }

    fun chatToOpponent(splitMessage: Array<String>) {
        val message = sewString(splitMessage)
        if (clientStatus === ClientStatus.PREGAME || clientStatus === ClientStatus.WAITING) {
            server.chatToAllPlayers("CHAT via server $clientName: $message")
        } else {
            singleGameServer!!.sendToPlayers("CHAT via server $clientName: $message")
        }
    }


    fun handleGoCommand(splitMessage: Array<String>) {
        dim = Integer.parseInt(splitMessage[1])
        server.addToWaitingList(this, dim)
        writeToClient("CHAT server - Great success! You have set your status to WAITING. Please wait for another player, " + clientName!!)
    }

    fun handleTableflipCommand() {
        singleGameServer!!.executeTurnTableflip(this)
    }

    fun handleExitCommand() {
        server.eraseClient(this)
    }

    fun handleMoveCommand(splitMessage: Array<String>) {
        val x = Integer.parseInt(splitMessage[1])
        val y = Integer.parseInt(splitMessage[2])
        singleGameServer!!.executeTurnMove(x, y, this)
        writeToClient("CHAT server - Great success! You have set your moved a stone to $x,$y")
        writeToClient("CHAT Turn finished, so don't try anything funny " + clientName!!)
    }

    fun handlePassCommand(splitMessage: Array<String>) {
        singleGameServer!!.executeTurnPass(this)

    }

    @Override
    fun equals(o: Object?): Boolean {
        if (this === o) return true
        if (o == null || getClass() !== o!!.getClass()) return false

        val that = o as ClientHandler?

        return if (uuid != null) uuid!!.equals(that!!.uuid) else that!!.uuid == null
    }

    @Override
    fun hashCode(): Int {
        return if (uuid != null) uuid!!.hashCode() else 0
    }

    fun handleScoreCommand() {
        writeToClient(singleGameServer!!.executeScore())
    }
}