package com.nedap.university.go.gocommands.clientToServer;

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
                clientHandler.chatToAll(splitMessage);
            case INGAME_NOT_TURN:
            case INGAME_TURN:
                clientHandler.chatToOpponent(splitMessage);
                break;
            default:
                cannotExecute();
                break;
        }

    }

    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot CHAT, you've broken the program if you've reached this position. ");
    }

}
