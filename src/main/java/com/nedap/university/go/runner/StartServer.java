package com.nedap.university.go.runner;

import com.nedap.university.go.client.GoClient;
import com.nedap.university.go.server.GoServer;

import java.io.IOException;


/**
 * Main class for playing the game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class StartServer {

    public static void main(String[] args) throws IOException {


        if (args.length == 1) {
            GoServer server = new GoServer(Integer.parseInt(args[0]));
            server.start();
        } else {
            GoClient client = new GoClient(args[0], Integer.parseInt(args[1]));
            client.start();
        }
    }

}
