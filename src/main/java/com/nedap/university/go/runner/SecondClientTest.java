package com.nedap.university.go.runner;

import com.nedap.university.go.client.GoClient;

import java.io.IOException;

/**
 * Class to add a second client to the game later on. Only for testing.
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class SecondClientTest {

    public static int serverPort;
    public static String hostName;

    public static void main(String[] args) throws IOException {
        try {

            hostName = args[0];
            serverPort = Integer.parseInt(args[1]);

        } catch (NumberFormatException e) {
            System.out.println("wrong input. correct input has 2 arguments divided by a space: ipadress port \n"
                    + "For starting own server, enter '0.0.0.0' as ipaddress.");
        }
        GoClient client = new GoClient(hostName, serverPort);
        client.start();
    }


}
