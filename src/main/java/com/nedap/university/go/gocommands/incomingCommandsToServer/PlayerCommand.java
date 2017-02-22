package com.nedap.university.go.gocommands.incomingCommandsToServer;

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
                if (splitMessage.length == 2 && checkName(splitMessage[1]) && clientHandler.getClientName() == null) {
                    clientHandler.handlePlayerCommand(splitMessage);
                } else {
                    clientHandler.writeToClient("WARNING Please enter PLAYER followed by a lowercase name. " + clientHandler.getClientName() +
                            ", name requirements: \n- name < 20 characters \n- name may only consist out of digits and letters. " +
                            "\n Or you already have entered a name.");
                }
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

    /**
     * checks whether the inputname is correct
     *
     * @param name string
     * @return boolean
     */
    private boolean checkName(String name) {
        return !((name.length() > 20) | name.matches(".*\\W+.*"));
    }

}
