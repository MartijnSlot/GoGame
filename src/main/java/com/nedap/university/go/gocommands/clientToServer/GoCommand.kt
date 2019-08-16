package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

class GoCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage

    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            PREGAME -> if (splitMessage.length === 2 && checkDim(splitMessage[1])) {
                clientHandler.handleGoCommand(splitMessage)
            } else {
                clientHandler.writeToClient("WARNING Status not changed, you are still pregame for a game. Please enter GO boardsize. ")
            }
            else -> cannotExecute()
        }
    }

    protected fun cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot GO, you're already GOne, or you've already GO'd. ")
    }

    /**
     * checks whether the given dimension is parsable and correct
     *
     * @param input string
     * @return boolean
     */
    private fun checkDim(input: String): Boolean {
        var dimIsOk = true
        val parsedInput: Int
        if (!isParsable(input)) {
            dimIsOk = false
        } else {
            parsedInput = Integer.parseInt(input)
            if (parsedInput % 2 == 0 || parsedInput < 5 || parsedInput > 131) {
                dimIsOk = false
            }
        }
        return dimIsOk
    }

    /**
     * checks whether a string input can be parsed to Integer
     *
     * @param input string
     * @return boolean
     */
    private fun isParsable(input: String): Boolean {
        try {
            Integer.parseInt(input)
        } catch (e: NumberFormatException) {
            return false
        }

        return true
    }

}
