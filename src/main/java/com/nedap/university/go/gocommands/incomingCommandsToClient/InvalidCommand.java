package com.nedap.university.go.gocommands.incomingCommandsToClient;

import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class InvalidCommand extends Command {

    public InvalidCommand(String[] splitMessage, ServerHandler serverHandler) {

    }

    @Override
    public void execute() {

    }

    @Override
    protected void cannotExecute() {
        return null;
    }
}