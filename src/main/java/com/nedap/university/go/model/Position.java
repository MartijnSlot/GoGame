package com.nedap.university.go.model;

import java.util.Objects;

/**
 * Class for creating a position on a GO board. 
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class Position {

	private int x;
	private int y;

	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}

	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Position)) return false;
		if (o == this) return true;
		return ((Position) o).getX() == this.x && ((Position) o).getY() == this.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

}

