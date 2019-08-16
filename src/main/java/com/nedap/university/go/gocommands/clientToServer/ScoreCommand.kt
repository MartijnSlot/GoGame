package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

/**
 * Created by martijn.slot on 21/02/2017.
 */
class ScoreCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        clientHandler.handleScoreCommand()
    }
}