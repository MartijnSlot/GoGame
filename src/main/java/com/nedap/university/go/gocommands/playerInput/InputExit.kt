package com.nedap.university.go.gocommands.playerInput

import com.nedap.university.go.client.GoClient
import com.nedap.university.go.gocommands.Command

/**
 * Created by martijn.slot on 24/02/2017.
 */
class InputExit(splitMessage: Array<String>, private val goClient: GoClient) : Command() {

    init {
        this.splitMessage = splitMessage

    }

    @Override
    fun execute() {
        goClient.handleExitFromPlayer(splitMessage)
    }
}
