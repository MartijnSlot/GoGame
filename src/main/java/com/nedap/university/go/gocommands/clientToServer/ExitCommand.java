package com.nedap.university.go.gocommands.clientToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class ExitCommand extends Command {

    private ClientHandler clientHandler;

    public ExitCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case PREGAME:
            case WAITING:
            case INGAME_NOT_TURN:
            case INGAME_TURN:
                if (splitMessage.length == 1) {
                    clientHandler.handleExitCommand(splitMessage);
                } else {
                    clientHandler.writeToClient("WARNING don't put any arguments after EXIT. ");
                }
                break;
            default:
                cannotExecute();
                break;
        }
    }

    protected void cannotExecute() {
        clientHandler.writeToClient("CHAT Exit cannot lead to this.");
    }

}
