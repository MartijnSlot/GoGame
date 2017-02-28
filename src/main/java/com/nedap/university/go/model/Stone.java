package com.nedap.university.go.model;

/**
 * Represents a Stone in a GO game. Three possible values:
 * Stone.BLACK, Stone.WHITE and Stone.EMPTY.
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public enum Stone {
    
    EMPTY, BLACK, WHITE;

    
    public String toString(){
    	if (this == BLACK) {
    		return "black";
    	} else if (this == WHITE) {
    		return "white";
    	}
    	return ".";
    }

    public String toTUIString(){
        if (this == BLACK) {
            return "B";
        } else if (this == WHITE) {
            return "W";
        }
        return ".";
    }
}
