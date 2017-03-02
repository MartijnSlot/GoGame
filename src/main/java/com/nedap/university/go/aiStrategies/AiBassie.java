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
        String move = "Asjemenou, dit mag ie niet printen! ";
        Map<Position, Point> points = game.getBoard().getPoints();
        for (Position a : points.keySet()) {
            if (game.moveAllowed(a.getX(), a.getY())) {
                move = "MOVE " + a.getX() + " " + a.getY();
            } else {
                move = "PASS";
            }
        }
        return move;

    }
}
