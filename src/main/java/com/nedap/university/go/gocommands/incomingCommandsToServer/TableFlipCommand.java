package com.nedap.university.go.gocommands.incomingCommandsToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class TableFlipCommand extends Command {

    private ClientHandler clientHandler;

    public TableFlipCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case INGAME_NOT_TURN:
            case INGAME_TURN:
                if (splitMessage.length == 1) {
                    clientHandler.handleTableflipCommand();
                } else {
                    clientHandler.writeToClient("WARNING tableflip incomplete, table too heavy. ");
                }
                break;
            default:
                cannotExecute();
                break;
        }
    }

    @Override
    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot tableflip. You must be in a game. ");
    }
}

