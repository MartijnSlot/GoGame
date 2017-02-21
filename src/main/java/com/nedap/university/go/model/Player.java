package com.nedap.university.go.model;

/**
 * Class for creating a player in a GO game. 
 * 
 * @author Martijn Slot
 * @version 1.0
 */
public class Player {

	private Stone stone;
	public boolean pass;
	public boolean winner;
	private int score;
	private boolean turn;
	
	public Player(Stone stone) {
		this.stone = stone;
		this.pass = false;
		this.winner = false;
		this.turn = false;
	}
	
	public Stone getStone() {
		return stone;
	}

	public boolean getTurn() { return turn; }

	public void setTurn(boolean turn) { this.turn = turn; }

	public void makeMove(Board board, Position pos) {
		board.setPoint(pos, this.getStone());	
		pass = false;
	}
	
	public void passes() {
		this.pass = true;
	}

	public void isWinner() {
		this.winner = true;
	}

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
		return score;
	}
}


