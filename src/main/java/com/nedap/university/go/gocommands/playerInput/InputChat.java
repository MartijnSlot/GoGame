package com.nedap.university.go.gocommands.playerInput;

import com.nedap.university.go.client.GoClient;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 02/03/2017.
 */
public class InputChat extends Command {

    private final GoClient goClient;
    private final String[] splitMessage;

    public InputChat(String[] splitMessage, GoClient goClient) {

        super();
        this.splitMessage = splitMessage;
        this.goClient = goClient;
    }

    @Override
    public void execute() {
        goClient.handleChatCommandFromPlayer(splitMessage);
    }

}
