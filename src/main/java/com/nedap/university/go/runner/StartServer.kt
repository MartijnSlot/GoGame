package com.nedap.university.go.runner

import com.nedap.university.go.client.GoClient
import com.nedap.university.go.server.GoServer

import java.io.IOException


/**
 * Main class for playing the game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
object StartServer {

    @Throws(IOException::class)
    fun main(args: Array<String>) {


        if (args.size == 1) {
            val server = GoServer(Integer.parseInt(args[0]))
            server.start()
        } else {
            val client = GoClient(args[0], Integer.parseInt(args[1]))
            client.start()
        }
    }

}
