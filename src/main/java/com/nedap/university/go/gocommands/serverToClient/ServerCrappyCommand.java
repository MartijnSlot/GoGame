package com.nedap.university.go.gocommands.serverToClient;

import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.gocommands.Protocol;

/**
 * Created by martijn.slot on 24/02/2017.
 */
public class ServerCrappyCommand extends Command {

    public ServerCrappyCommand(String[] splitMessage) {

        super();
        this.splitMessage = splitMessage;
    }

    @Override
    public void execute() {
        System.out.println("Servermessage: " + sewString(splitMessage)  + " is not a correct message, server is fu.");
    }

    /**
     * sews a split string back together
     * @param splitMessage list of strings
     * @return string
     */
    private String sewString(String[] splitMessage) {
        return String.join(Protocol.DELIMITER, splitMessage);
    }
}
