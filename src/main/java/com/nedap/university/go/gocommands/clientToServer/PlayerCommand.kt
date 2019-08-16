package com.nedap.university.go.gocommands.clientToServer

import com.nedap.university.go.gocommands.Command
import com.nedap.university.go.server.ClientHandler

class PlayerCommand(splitMessage: Array<String>, private val clientHandler: ClientHandler) : Command() {

    init {
        this.splitMessage = splitMessage
    }

    @Override
    fun execute() {
        when (clientHandler.getClientStatus()) {
            PREGAME -> if (splitMessage.length === 2 && checkName(splitMessage[1]) && clientHandler.getClientName() == null) {
                clientHandler.handlePlayerCommand(splitMessage)
            } else {
                clientHandler.writeToClient("WARNING Please enter PLAYER followed by a lowercase name. " + clientHandler.getClientName() +
                    ", name requirements: \n- name < 20 characters \n- name may only consist out of digits and letters. " +
                    "\n Or you already have entered a name.")
            }
            else -> cannotExecute()
        }
    }

    protected fun cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot set name, please enter CANCEL first to cancel become a PREGAME-player again. Then you can enter PLAYER name. ")
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
