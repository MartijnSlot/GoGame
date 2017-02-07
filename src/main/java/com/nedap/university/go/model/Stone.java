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
    
	/**
     * @return the other Stone is this Stone is not EMPTY or EMPTY
     */
    public Stone other() {
        if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }
    
    public String toString(){
    	if (this == BLACK) {
    		return "B";
    	} else if (this == WHITE) {
    		return "W";
    	}
    	return ".";
    }
}
