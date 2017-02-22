package com.nedap.university.go.gocommands.clientToServer;

import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.server.ClientHandler;

public class GoCommand extends Command {

    private ClientHandler clientHandler;

    public GoCommand(String[] splitMessage, ClientHandler clientHandler) {
        super();
        this.splitMessage = splitMessage;
        this.clientHandler = clientHandler;

    }

    @Override
    public void execute() {
        switch (clientHandler.getClientStatus()) {
            case PREGAME:
                if (splitMessage.length == 2 && checkDim(splitMessage[1])) {
                    clientHandler.handleGoCommand(splitMessage);
                } else {
                    clientHandler.writeToClient("WARNING Status not changed, you are still pregame for a game. Please enter GO boardsize. ");
                }
                break;
            default:
                cannotExecute();
                break;
        }
    }

    protected void cannotExecute() {
        clientHandler.writeToClient("WARNING Cannot GO, you're already GOne, or you've already GO'd. ");
    }

    /**
     * checks whether the given dimension is parsable and correct
     *
     * @param input string
     * @return boolean
     */
    private boolean checkDim(String input) {
        boolean dimIsOk = true;
        int parsedInput;
        if (!isParsable(input)) {
            dimIsOk = false;
        } else {
            parsedInput = Integer.parseInt(input);
            if (parsedInput % 2 == 0 || parsedInput < 5 || parsedInput > 131) {
                dimIsOk = false;
            }
        }
        return dimIsOk;
    }

    /**
     * checks whether a string input can be parsed to Integer
     *
     * @param input string
     * @return boolean
     */
    private boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
