package com.nedap.university.go.gocommands.clientToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class PassCommand extends Command {

    private ClientHandler clientHandler;

    public PassCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case INGAME_TURN:
                clientHandler.handlePassCommand(splitMessage);
                break;
            default:
                cannotExecute();
                break;
        }
    }

    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Don't PASS! Wait for your turn like the rest of us. ");
    }

}
