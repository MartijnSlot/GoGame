package com.nedap.university.go.gocommands.playerInput;

import com.nedap.university.go.client.GoClient;
import com.nedap.university.go.gocommands.Command;

/**
 * Created by martijn.slot on 24/02/2017.
 */
public class InputMove extends Command {

    private final GoClient goClient;

    public InputMove(String[] splitMessage, GoClient goClient) {

        super();
        this.splitMessage = splitMessage;
        this.goClient = goClient;

    }

    @Override
    public void execute() {
        if (splitMessage.length == 3 && isParsable(splitMessage[1]) && isParsable(splitMessage[2])) {
            int x = Integer.parseInt(splitMessage[1]);
            int y = Integer.parseInt(splitMessage[2]);
            goClient.handleMoveFromPlayer(x, y);
        } else {
            System.out.println("Doe ff een goeie move dan, de syntax is al fout mafkees. ");
        }
    }

    private boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
