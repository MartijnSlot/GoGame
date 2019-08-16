package com.nedap.university.go.aiStrategies

import com.nedap.university.go.controller.Game

interface Strategy {

    fun determineMove(game: Game): String

}
