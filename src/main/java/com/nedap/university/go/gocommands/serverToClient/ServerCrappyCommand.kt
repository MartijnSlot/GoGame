package com.nedap.university.go.gocommands.serverToClient

import com.nedap.university.go.client.ServerHandler
import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.gocommands.Protocol

/**
 * Created by martijn.slot on 24/02/2017.
 */
class ServerCrappyCommand(splitMessage: Array<String>) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        System.out.println("Servermessage: " + sewString(splitMessage) + " is not a correct message, server is fu.")
    }

    /**
     * sews a split string back together
     * @param splitMessage list of strings
     * @return string
     */
    private fun sewString(splitMessage: Array<String>): String {
        return String.join(Protocol.DELIMITER, splitMessage)
    }
}
