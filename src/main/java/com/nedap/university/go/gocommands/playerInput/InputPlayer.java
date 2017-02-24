package com.nedap.university.go.gocommands.playerInput;

import com.nedap.university.go.client.GoClient;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 24/02/2017.
 */
public class InputPlayer extends Command {

    private final GoClient goClient;

    public InputPlayer(String[] splitMessage, GoClient goClient) {

        super();
        this.splitMessage = splitMessage;
        this.goClient = goClient;

    }

    @Override
    public void execute() {
        if(splitMessage.length == 2 && checkName(splitMessage[1])) {
            goClient.handlePlayerCommandFromPlayer(splitMessage);
        } else {
            System.out.println("Doe ff een normale input, joh, mafketel. ");
        }

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
