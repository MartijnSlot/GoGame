package com.nedap.university.go.model;

/**
 * Class for creating a player in a GO game. 
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class Player {

	public String name;
	public Stone stone;
	public boolean pass;
	public boolean winner;
	
	public Player(String name, Stone stone) {
		this.name = name;
		this.stone = stone;
		this.pass = false;
		this.winner = false;
	}
	
	/**
	 * getter for the player name
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * getter for the stone
	 * @return Stone
	 */
	public Stone getStone() {
		return stone;
	}

	/**
	 * puts a move on a board
	 * @param board
	 * @param pos
	 */
	public void makeMove(Board board, Position pos) {
		board.setPoint(pos, this.getStone());	
		pass = false;
	}
	
	/**
	 * player passes
	 */
	public void passes() {
		this.pass = true;
	}

	/**
	 * player is winner
	 */
	public void isWinner() {
		this.winner = true;
	}

	
}


