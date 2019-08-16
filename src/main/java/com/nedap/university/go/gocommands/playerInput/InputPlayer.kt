package com.nedap.university.go.gocommands.playerInput

import com.nedap.university.go.client.GoClient
import com.nedap.university.go.gocommands.Command

/**
 * Created by martijn.slot on 24/02/2017.
 */
class InputPlayer(splitMessage: Array<String>, private val goClient: GoClient) : Command() {

    init {
        this.splitMessage = splitMessage

    }

    @Override
    fun execute() {
        if (splitMessage.length === 2 && checkName(splitMessage[1])) {
            goClient.handlePlayerCommandFromPlayer(splitMessage)
        } else {
            System.out.println("Doe ff een normale input, joh, mafketel. ")
        }

    }

    /**
     * checks whether the inputname is correct
     *
     * @param name string
     * @return boolean
     */
    private fun checkName(name: String): Boolean {
        return !((name.length() > 20) or name.matches(".*\\W+.*"))
    }
}
