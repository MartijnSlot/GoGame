package com.nedap.university.go.gocommands;

/**
 * Created by martijn.slot on 21/02/2017.
 */
public abstract class Command {

    public String[] splitMessage;

    public abstract void execute();

}

