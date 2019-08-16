package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class TableFlipCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            INGAME_NOT_TURN, INGAME_TURN -> if (splitMessage.length === 1) {
                clientHandler.handleTableflipCommand()
            } else {
                clientHandler.writeToClient("WARNING tableflip incomplete, table too heavy. ")
            }
            else -> cannotExecute()
        }
    }

    private fun cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot tableflip. You must be in a game. ")
    }
}

