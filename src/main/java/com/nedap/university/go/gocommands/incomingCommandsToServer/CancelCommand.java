package com.nedap.university.go.gocommands.incomingCommandsToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.*;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class CancelCommand extends Command {

    private ClientHandler clientHandler;

    public CancelCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case WAITING:
                if (splitMessage.length == 1) {
                    clientHandler.handleCancelCommand(splitMessage);
                } else {
                    clientHandler.writeToClient("WARNING Wrong input. Status not changed, you are still waiting for a game. ");
                }
                break;
            default:
                cannotExecute();
                break;
        }
    }

    @Override
    public void cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot CANCEL, you are not waiting for a game. ");
    }
}
