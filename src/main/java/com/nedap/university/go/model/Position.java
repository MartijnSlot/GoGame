package com.nedap.university.go.model;

import java.util.Objects;

/**
 * Class for creating a position on a GO board. 
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class Position {

	int x;
	int y;

	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}

	/**
	 * getter for the columns (x)
	 * @return int
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * getter for the rows (y)
	 * @return
	 */
	public int getY() {
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

