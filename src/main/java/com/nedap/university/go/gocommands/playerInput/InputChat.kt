package com.nedap.university.go.gocommands.playerInput

import com.nedap.university.go.client.GoClient
import com.nedap.university.go.gocommands.Command

/**
 * Created by martijn.slot on 02/03/2017.
 */
class InputChat(private val splitMessage: Array<String>, private val goClient: GoClient) : Command() {

    @Override
    fun execute() {
        goClient.handleChatCommandFromPlayer(splitMessage)
    }

}
