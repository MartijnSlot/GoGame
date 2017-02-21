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
                //TODO cool stuff
                break;
            default:
                cannotExecute();
                break;
        }
    }

    @Override
    protected void cannotExecute() {
        return null;
    }

}
