package com.nedap.university.go.gocommands.incomingCommandsToClient;

import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class EndCommand extends Command {
    public EndCommand(String[] splitMessage, ServerHandler serverHandler) {
        super();
    }

    @Override
    public void execute() {

    }

    @Override
    protected void cannotExecute() {
        return null;
    }
}
