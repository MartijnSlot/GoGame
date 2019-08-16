package com.nedap.university.go.aiStrategies

import com.nedap.university.go.controller.Game

/**
 * Created by martijn.slot on 02/03/2017.
 */
class AiKasparov : Strategy {

    @Override
    fun determineMove(game: Game): String {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return "PASS"
    }
}
