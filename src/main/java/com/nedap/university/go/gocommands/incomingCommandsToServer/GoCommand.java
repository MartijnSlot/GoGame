package com.nedap.university.go.gocommands.incomingCommandsToServer;

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
                clientHandler.enterDimension(splitMessage);
                break;
            default:
                cannotExecute();
                break;
        }
    }

    @Override
    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot GO, you're already GOne, or you've already GO'd. ");
    }

}
