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

    private String name;
    private ServerHandler serverHandler;

    public AiBassie(ServerHandler serverHandler){
        this.name = "clownbassie";
        this.serverHandler = serverHandler;
    }

    @Override
    public String determineMove(Game game) {
        switch (serverHandler.getClientStatus()) {
            case PREGAME:
            case WAITING:
                return "CHAT not in game.";
            case INGAME_NOT_TURN:
                return "CHAT not your turn.";
            case INGAME_TURN:
                return determineMoveTurn(game);
            default:
                break;
        }
        System.out.println("De AI mag hier niet komen.");
        return null;
    }


    private String determineMoveTurn(Game game) {
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
