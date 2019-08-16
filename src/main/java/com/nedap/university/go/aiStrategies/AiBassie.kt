package com.nedap.university.go.aiStrategies


import com.nedap.university.go.client.ServerHandler
import com.nedap.university.go.controller.Game
import com.nedap.university.go.model.Point
import com.nedap.university.go.model.Position

/**
 * Created by martijn.slot on 02/03/2017.
 */
class AiBassie : Strategy {

    @Override
    fun determineMove(game: Game): String {
        var move = "Asjemenou, dit mag ie niet printen! "
        val points = game.getBoard().getPoints()
        for (a in points.keySet()) {
            if (game.moveAllowed(a.getX(), a.getY())) {
                move = "MOVE " + a.getX() + " " + a.getY()
            } else {
                move = "PASS"
            }
        }
        return move

    }
}
