package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class MoveCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage

    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            INGAME_TURN -> if (splitMessage.length === 3 && isParsable(splitMessage[1]) && isParsable(splitMessage[2])) {
                clientHandler.handleMoveCommand(splitMessage)
            } else {
                clientHandler.writeToClient("WARNING MOVE command syntax not OK. Please repeat. ")
            }
            else -> cannotExecute()
        }
    }

    protected fun cannotExecute() {
        clientHandler.writeToClient("WARNING Don't MOVE! Wait for your turn like the rest of us. ")
    }

    private fun isParsable(input: String): Boolean {
        try {
            Integer.parseInt(input)
        } catch (e: NumberFormatException) {
            return false
        }

        return true
    }

}
