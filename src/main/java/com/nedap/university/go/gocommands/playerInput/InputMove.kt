package com.nedap.university.go.gocommands.playerInput

import com.nedap.university.go.client.GoClient
import com.nedap.university.go.gocommands.Command

/**
 * Created by martijn.slot on 24/02/2017.
 */
class InputMove(splitMessage: Array<String>, private val goClient: GoClient) : Command() {

    init {
        this.splitMessage = splitMessage

    }

    @Override
    fun execute() {
        if (splitMessage.length === 3 && isParsable(splitMessage[1]) && isParsable(splitMessage[2])) {
            val x = Integer.parseInt(splitMessage[1])
            val y = Integer.parseInt(splitMessage[2])
            goClient.handleMoveFromPlayer(x, y)
        } else {
            System.out.println("Doe ff een goeie move dan, de syntax is al fout mafkees. ")
        }
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
