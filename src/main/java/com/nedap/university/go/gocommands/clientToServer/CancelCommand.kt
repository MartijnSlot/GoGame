package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.*

/**
 * Created by martijn.slot on 21/02/2017.
 */
class CancelCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            WAITING -> if (splitMessage.length === 1) {
                clientHandler.handleCancelCommand()
            } else {
                clientHandler.writeToClient("WARNING Wrong input. Status not changed, you are still waiting for a game. ")
            }
            else -> cannotExecute()
        }
    }


    internal fun cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot CANCEL, you are not waiting for a game. ")
    }
}
