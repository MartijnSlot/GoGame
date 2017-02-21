package com.nedap.university.go.gocommands.clientcommands;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

public class GoCommand extends Command {

    private ClientHandler clientHandler;

    public GoCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;

    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case PREGAME:
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
