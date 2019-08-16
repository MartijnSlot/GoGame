package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class ClientChatCommand(command: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = command
    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            PREGAME, WAITING -> clientHandler.chatToAll(splitMessage)
            INGAME_NOT_TURN, INGAME_TURN -> clientHandler.chatToOpponent(splitMessage)
            else -> cannotExecute()
        }

    }

    protected fun cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot CHAT, you've broken the program if you've reached this position. ")
    }

}
