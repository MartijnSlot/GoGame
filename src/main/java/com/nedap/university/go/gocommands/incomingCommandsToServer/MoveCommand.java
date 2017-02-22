package com.nedap.university.go.gocommands.incomingCommandsToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class MoveCommand extends Command {

    private ClientHandler clientHandler;

    public MoveCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;

    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case INGAME_TURN:
                if (splitMessage.length == 3 && isParsable(splitMessage[1]) && isParsable(splitMessage[2])) {
                    clientHandler.handleMoveCommand(splitMessage);
                } else {
                    clientHandler.writeToClient("WARNING MOVE command syntax not OK. Please repeat. ");
                }
                break;
            default:
                cannotExecute();
                break;
        }
    }

    @Override
    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Don't MOVE! Wait for your turn like the rest of us. ");
    }

    private boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
