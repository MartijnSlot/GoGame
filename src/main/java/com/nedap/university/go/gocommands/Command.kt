package com.nedap.university.go.gocommands

/**
 * Created by martijn.slot on 21/02/2017.
 */
abstract class Command {

    var splitMessage: Array<String>? = null

    abstract fun execute()

}

