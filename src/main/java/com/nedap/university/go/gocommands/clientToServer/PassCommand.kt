package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class PassCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            INGAME_TURN -> clientHandler.handlePassCommand(splitMessage)
            else -> cannotExecute()
        }
    }

    protected fun cannotExecute() {
        clientHandler.writeToClient("WARNING Don't PASS! Wait for your turn like the rest of us. ")
    }

}
