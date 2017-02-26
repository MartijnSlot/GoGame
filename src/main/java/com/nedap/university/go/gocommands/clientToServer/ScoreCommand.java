package com.nedap.university.go.gocommands.clientToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class ScoreCommand extends Command {

    private ClientHandler clientHandler;

    public ScoreCommand(String[] splitMessage, ClientHandler clientHandler) {

        super();
        this.clientHandler = clientHandler;
        this.splitMessage = splitMessage;
    }

    @Override
    public void execute() {
        clientHandler.handleScoreCommand();
    }
}