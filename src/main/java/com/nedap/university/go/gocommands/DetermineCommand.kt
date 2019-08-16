package com.nedap.university.go.gocommands

import com.nedap.university.go.client.GoClient
import com.nedap.university.go.client.ServerHandler
import com.nedap.university.go.gocommands.playerInput.*
import com.nedap.university.go.server.ClientHandler
import com.nedap.university.go.gocommands.clientToServer.*
import com.nedap.university.go.gocommands.serverToClient.*
import org.hamcrest.Matcher


/**
 * Created by martijn.slot on 21/02/2017.
 */
class DetermineCommand : Protocol {

    /**
     * When the client gets one of these commands, this method determines what to do next
     * @param message
     * @param serverHandler
     */
    fun determineClientCommand(message: String, serverHandler: ServerHandler): Command {
        val splitMessage = message.split(DELIMITER)
        val command: Command
        when (splitMessage[0]) {
            TABLEFLIPPED -> command = TableFlippedCommand(splitMessage, serverHandler)
            PASSED -> command = PassedCommand(splitMessage, serverHandler)
            CHAT -> command = ServerChatCommand(splitMessage, serverHandler)
            WARNING -> command = WarningCommand(splitMessage, serverHandler)
            READY -> command = ReadyCommand(splitMessage, serverHandler)
            VALID -> command = ValidCommand(splitMessage, serverHandler)
            INVALID -> command = InvalidCommand(splitMessage, serverHandler)
            END -> command = EndCommand(splitMessage, serverHandler)
            else -> command = ServerCrappyCommand(splitMessage)
        }
        return command
    }

    /**
     * When the client gets one of these commands, this method determines what to do next
     * @param message
     * @param clientHandler
     */
    fun determineServerCommand(message: String, clientHandler: ClientHandler): Command {
        val splitMessage = message.split(DELIMITER)
        val command: Command
        when (splitMessage[0]) {
            TABLEFLIP -> command = TableFlipCommand(splitMessage, clientHandler)
            PASS -> command = PassCommand(splitMessage, clientHandler)
            CHAT -> command = ClientChatCommand(splitMessage, clientHandler)
            EXIT -> command = ExitCommand(splitMessage, clientHandler)
            CANCEL -> command = CancelCommand(splitMessage, clientHandler)
            MOVE -> command = MoveCommand(splitMessage, clientHandler)
            PLAYER -> command = PlayerCommand(splitMessage, clientHandler)
            GO -> command = GoCommand(splitMessage, clientHandler)
            SCORE -> command = ScoreCommand(splitMessage, clientHandler)
            else -> command = ClientCrappyCommand(splitMessage, clientHandler)
        }
        return command
    }

    fun inputCommand(message: String, goClient: GoClient): Command {
        val splitMessage = message.split(DELIMITER)
        val command: Command
        when (splitMessage[0]) {
            MOVE -> command = InputMove(splitMessage, goClient)
            EXIT -> command = InputExit(splitMessage, goClient)
            PLAYER -> command = InputPlayer(splitMessage, goClient)
            GO -> command = InputGo(splitMessage, goClient)
            CHAT -> command = InputChat(splitMessage, goClient)
            else -> command = InputOtherCommands(splitMessage, goClient)
        }
        return command
    }
}
