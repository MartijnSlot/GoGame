package com.nedap.university.go.gocommands;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public class CrappyCommand extends Command {

    public CrappyCommand(String[] command) {
        execute();
    }

    @Override
    public void execute() {

    }

    protected void cannotExecute() {
    }
}
