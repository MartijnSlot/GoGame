package com.nedap.university.go.gocommands;

import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.gocommands.clientcommands.*;
import com.nedap.university.go.gocommands.servercommands.*;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class DetermineCommand implements Protocol {

    /**
     * When the client gets one of these commands, this method determines what to do next
     * @param command
     * @param serverHandler
     */
    public void determineClientCommand(String command, ServerHandler serverHandler) {
        String[] commands = command.split(Protocol.DELIMITER);
        switch (commands[0]) {
            case Protocol.TABLEFLIPPED:
                new TableFlippedCommand();
                break;
            case Protocol.PASSED:
                new PassedCommand();
                break;
            case Protocol.CHAT:
                new ClientChatCommand();
                break;
            case Protocol.WARNING:
                new WarningCommand();
                break;
            case Protocol.READY:
                new ReadyCommand();
                break;
            case Protocol.VALID:
                new ValidCommand();
                break;
            case Protocol.INVALID:
                new InvalidCommand();
                break;
            case Protocol.END:
                new EndCommand();
                break;
            default:
                new CrappyCommand(command);
                break;
        }

    }

    /**
     * When the client gets one of these commands, this method determines what to do next
     * @param command
     * @param clientHandler
     */
    public void determineServerCommand(String command, ClientHandler clientHandler) {
        String[] commands = command.split(Protocol.DELIMITER);
        switch (commands[0]) {
            case Protocol.TABLEFLIP:
                new TableFlipCommand();
                break;
            case Protocol.PASS:
                new PassCommand();
                break;
            case Protocol.CHAT:
                new ServerChatCommand();
                break;
            case Protocol.EXIT:
                new ExitCommand();
                break;
            case Protocol.CANCEL:
                new CancelCommand();
                break;
            case Protocol.MOVE:
                new MoveCommand();
                break;
            case Protocol.PLAYER:
                new PlayerCommand();
                break;
            case Protocol.GO:
                new GoCommand();
                break;
            default:
                new CrappyCommand(command);
                break;
        }

    }
}
