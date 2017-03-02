package com.nedap.university.go.aiStrategies;


import com.nedap.university.go.client.ServerHandler;
import com.nedap.university.go.controller.Game;
import com.nedap.university.go.model.Point;
import com.nedap.university.go.model.Position;

import java.util.Map;

/**
 * Created by martijn.slot on 02/03/2017.
 */
public class AiBassie implements Strategy {

    public AiBassie(){
    }

    @Override
    public String determineMove(Game game) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String move;
        Map<Position, Point> points = game.getBoard().getPoints();
        for (Position a : points.keySet()) {
            if (game.moveAllowed(a.getX(), a.getY())) {
                move = "MOVE " + a.getX() + " " + a.getY();
                return move;
            }
        }
        return "MOVE 1 1";

    }
}
