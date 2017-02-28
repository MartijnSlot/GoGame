package com.nedap.university.go.model;

/**
 * Class for creating a player in a GO game. 
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class Player {

	private Stone stone;
	private boolean pass;
	public boolean winner;
	
	public Player(Stone stone) {
		this.stone = stone;
		this.pass = false;
		this.winner = false;
	}
	
	public Stone getStone() {
		return stone;
	}

	public void makeMove(Board board, Position pos) {
		board.setPoint(pos, this.getStone());	
		pass = false;
	}
	
	public void passes() {
		this.pass = true;
	}

	public boolean isWinner() {
		return winner;
	}

	public boolean getPass() {
		return pass;
	}
}


