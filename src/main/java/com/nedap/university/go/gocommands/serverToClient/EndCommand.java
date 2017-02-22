package com.nedap.university.go.gocommands.serverToClient;

import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class EndCommand extends Command {

    private ServerHandler serverHandler;

    public EndCommand(String[] splitMessage, ServerHandler serverHandler) {

        super();
        this.splitMessage = splitMessage;
        this.serverHandler = serverHandler;
    }

    @Override
    public void execute() {
        serverHandler.handleEnd(splitMessage);
    }

}
