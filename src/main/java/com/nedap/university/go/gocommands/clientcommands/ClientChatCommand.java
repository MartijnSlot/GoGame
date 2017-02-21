package com.nedap.university.go.gocommands.clientcommands;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class ClientChatCommand extends Command {

    private ClientHandler clientHandler;

    public ClientChatCommand(String[] command, ClientHandler clientHandler) {
        super();
        this.splitMessage = command;
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case PREGAME:
            case WAITING:
            case INGAME_NOT_TURN:
            case INGAME_TURN:

                break;
            default:
                cannotExecute();
                break;
        }

    }

    @Override
    protected void cannotExecute() {

    }

}
