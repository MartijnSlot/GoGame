package com.nedap.university.go.model;

/**
 * Class for creating a point in a GO game. 
 * tiny bit obsolete because it only has 1 field.
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class Point {
	
	private Stone stone;
	
	public Point(Stone s){
		this.stone = s;
		
	}
	
	Stone getStone() {
		return stone;
	}

}
