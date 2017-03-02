package com.nedap.university.go.gocommands.playerInput;

import com.nedap.university.go.client.GoClient;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 24/02/2017.
 */
public class InputGo extends Command {

    private final GoClient goClient;

    public InputGo(String[] splitMessage, GoClient goClient) {

        super();
        this.splitMessage = splitMessage;
        this.goClient = goClient;

    }

    @Override
    public void execute() {
        goClient.handleGoFromPlayer(splitMessage);
    }
}
