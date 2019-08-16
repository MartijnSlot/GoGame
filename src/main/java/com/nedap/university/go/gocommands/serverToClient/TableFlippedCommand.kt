package com.nedap.university.go.gocommands.serverToClient

import com.nedap.university.go.client.ServerHandler
import com.nedap.university.go.gocommands.Command

/**
 * Created by martijn.slot on 21/02/2017.
 */
class TableFlippedCommand(splitMessage: Array<String>, private val serverHandler: ServerHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        serverHandler.handleTableFlipped(splitMessage)

    }
}
