package com.nedap.university.go.gocommands;

import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.server.ClientHandler;
import com.nedap.university.go.gocommands.clientToServer.*;
import com.nedap.university.go.gocommands.serverToClient.*;


/**
 * Created by martijn.slot on 21/02/2017.
 */
public class DetermineCommand implements Protocol {

    /**
     * When the client gets one of these commands, this method determines what to do next
     * @param message
     * @param serverHandler
     */
    public static Command determineClientCommand(String message, ServerHandler serverHandler) {
        String[] splitMessage = message.split(DELIMITER);
        Command command;
        switch (splitMessage[0]) {
            case TABLEFLIPPED:
                command = new TableFlippedCommand(splitMessage, serverHandler);
                break;
            case PASSED:
                command = new PassedCommand(splitMessage, serverHandler);
                break;
            case CHAT:
                command = new ServerChatCommand(splitMessage, serverHandler);
                break;
            case WARNING:
                command = new WarningCommand(splitMessage, serverHandler);
                break;
            case READY:
                command = new ReadyCommand(splitMessage, serverHandler);
                break;
            case VALID:
                command = new ValidCommand(splitMessage, serverHandler);
                break;
            case INVALID:
                command = new InvalidCommand(splitMessage, serverHandler);
                break;
            case END:
                command = new EndCommand(splitMessage, serverHandler);
                break;
            default:
                command = new CrappyCommand(splitMessage);
                break;
        }
        return command;
    }

    /**
     * When the client gets one of these commands, this method determines what to do next
     * @param message
     * @param clientHandler
     */
    public static Command determineServerCommand(String message, ClientHandler clientHandler) {
        String[] splitMessage = message.split(DELIMITER);
        Command command;
        switch (splitMessage[0]) {
            case TABLEFLIP:
                command = new TableFlipCommand(splitMessage, clientHandler);
                break;
            case PASS:
                command = new PassCommand(splitMessage, clientHandler);
                break;
            case CHAT:
                command = new ClientChatCommand(splitMessage, clientHandler);
                break;
            case EXIT:
                command = new ExitCommand(splitMessage, clientHandler);
                break;
            case CANCEL:
                command = new CancelCommand(splitMessage, clientHandler);
                break;
            case MOVE:
                command = new MoveCommand(splitMessage, clientHandler);
                break;
            case PLAYER:
                command = new PlayerCommand(splitMessage, clientHandler);
                break;
            case GO:
                command = new GoCommand(splitMessage, clientHandler);
                break;
            default:
                command = new CrappyCommand(splitMessage);
                break;
        }
        return command;
    }
}
