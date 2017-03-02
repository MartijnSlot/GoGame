package com.nedap.university.go.aiStrategies;

import com.nedap.university.go.controller.Game;

/**
 * Created by martijn.slot on 02/03/2017.
 */
public class AiKasparov implements Strategy {

    @Override
    public String determineMove(Game game) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "PASS";
    }
}
