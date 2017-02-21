package com.nedap.university.go.gocommands.clientcommands;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

public class PlayerCommand extends Command {

    private ClientHandler clientHandler;

    public PlayerCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;
    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case PREGAME:
                clientHandler.enterPlayerName(splitMessage);
                clientHandler.writeToClient("CHAT server - Great success! You have entered your name: " + splitMessage[1]);
                break;
            default:
                cannotExecute();
                break;
        }
    }

    @Override
    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot set name, please enter CANCEL first to cancel become a PREGAME-player again. Then you can enter PLAYER name. ");
    }

}
