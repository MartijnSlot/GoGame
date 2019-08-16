package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class ExitCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            PREGAME, WAITING, INGAME_NOT_TURN, INGAME_TURN -> if (splitMessage.length === 1) {
                clientHandler.handleExitCommand()
            } else {
                clientHandler.writeToClient("WARNING don't put any arguments after EXIT. ")
            }
            else -> cannotExecute()
        }
    }

    protected fun cannotExecute() {
        clientHandler.writeToClient("CHAT Exit cannot lead to this.")
    }

}
