package com.nedap.university.go.server

import java.net.*
import java.util.*
import java.io.*

/**
 * Class for creating a general server.
 *
 * @author Martijn Slot
 * @version 1.0
 */

class GoServer(port: Int) : Thread() {

    private var serverSocket: ServerSocket? = null
    private val pendingClients = HashMap()
    private val clientSet = HashSet()
    private var clientCounter = 0

    init {
        System.out.println("Starting server on port $port")
        try {
            val maxClients = 50
            serverSocket = ServerSocket(port, maxClients)
        } catch (e: IOException) {
            System.out.print("Could not listen on port $port")
        }

    }

    @Override
    fun run() {
        System.out.println("Waiting for clients...")

        while (!serverSocket!!.isClosed()) {
            try {
                if (clientCounter < 500) {
                    val clientHandler = ClientHandler(serverSocket!!.accept(), this)
                    clientSet.add(clientHandler)
                    clientCounter += 1
                    System.out.println("Client Accepted! Client count: $clientCounter")
                    clientHandler.start()
                } else {
                    System.out.println("Too many Clients!, restart server!")
                }
            } catch (e: IOException) {
                System.out.println("Cannot accept client.")
            }

        }
    }

    /**
     * Sends out a chatmessage to all players on server
     * @param message
     * @throws IOException
     */

    fun chatToAllPlayers(message: String) {
        for (clientHandler in clientSet) {
            clientHandler.writeToClient(message)
        }
    }

    /**
     * enter the client into the server list, then into the server waiting list
     * if there is another client with the same dimension, it will start a game
     * @param client
     * @param dim
     * @throws IOException
     */
    internal fun addToWaitingList(client: ClientHandler, dim: Int) {
        val clientHandlers = ArrayList()
        clientHandlers.add(client)

        if (pendingClients.containsKey(dim)) {
            pendingClients.get(dim).addAll(clientHandlers)
        } else {
            pendingClients.put(dim, clientHandlers)
        }

        client.setClientStatus(ClientStatus.WAITING)
        matchWaitingPlayers()
    }

    private fun matchWaitingPlayers() {
        for (dimBoard in pendingClients.keySet()) {
            if (pendingClients.get(dimBoard).size() === 2) {
                startNewGame(dimBoard)
                break
            }
        }
    }

    private fun startNewGame(dimBoard: Int) {
        val ch1 = pendingClients.get(dimBoard).get(0)
        val ch2 = pendingClients.get(dimBoard).get(1)
        pendingClients.remove(dimBoard)

        ch1.setClientStatus(ClientStatus.INGAME_TURN)
        ch2.setClientStatus(ClientStatus.INGAME_NOT_TURN)
        try {
            val singleGameServer = SingleGameServer(ch1, ch2, dimBoard)
            singleGameServer.startGame(ch1, ch2, dimBoard)
            ch1.setSingleGameServer(singleGameServer)
            ch2.setSingleGameServer(singleGameServer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * removes a client from the server clientlist
     * @param clientHandler to remove
     */
    internal fun eraseClient(clientHandler: ClientHandler) {
        try {
            clientCounter -= 1
            System.out.println("Client removed! Client count: $clientCounter")
            pendingClients.get(clientHandler.getDim()).remove(clientHandler)
            clientSet.remove(clientHandler)
        } catch (npe: NullPointerException) {
            System.out.println("Client " + clientHandler.getClientName() + " erased.")
        }

    }

    internal fun statusWaitingToInitial(clientHandler: ClientHandler) {
        pendingClients.get(clientHandler.getDim()).remove(clientHandler)
    }

}
