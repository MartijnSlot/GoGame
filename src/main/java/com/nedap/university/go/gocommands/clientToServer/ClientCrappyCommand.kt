package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.gocommands.Protocol
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class ClientCrappyCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        clientHandler.writeToClient("CHAT server - " + sewString(splitMessage) + " is not a correct message, please try again.")
    }

    /**
     * sews a splitted string back together
     * @param splitMessage list of strings
     * @return string
     */
    private fun sewString(splitMessage: Array<String>): String {
        return String.join(Protocol.DELIMITER, splitMessage)
    }
}
