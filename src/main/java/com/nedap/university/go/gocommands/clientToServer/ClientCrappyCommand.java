package com.nedap.university.go.gocommands.clientToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.gocommands.Protocol;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class ClientCrappyCommand extends Command {

    private ClientHandler clientHandler;

    public ClientCrappyCommand(String[] splitMessage, ClientHandler clientHandler) {

        super();
        this.clientHandler = clientHandler;
        this.splitMessage = splitMessage;
    }

    @Override
    public void execute() {
        clientHandler.writeToClient("CHAT server - " + sewString(splitMessage)  + " is not a correct message, please try again.");
    }

    /**
     * sews a splitted string back together
     * @param splitMessage list of strings
     * @return string
     */
    private String sewString(String[] splitMessage) {
        return String.join(Protocol.DELIMITER, splitMessage);
    }
}
