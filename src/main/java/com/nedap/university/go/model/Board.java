




package com.nedap.university.go.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a board in a Go game.
 *
 * @author Martijn Slot
 * @version 1.0
 */
public class Board {

	private int dim;
	private Map<Position, Point> points = new HashMap<>();
	private int blackScore = 0;
	private int whiteScore = 0;

	/**
	 * constructor of board of size dim * dim, containing only EMPTY fields.
	 * @param dim
	 */

	public Board(int dim) {
		this.dim = dim;
		for (int x = 0; x < dim; x++) {
			for (int y = 0; y < dim; y++) {
				this.points.put(new Position(x,y), new Point(Stone.EMPTY));
			}
		}
	}

	public int getDim() {
		return dim;
	}

	public Map<Position, Point> getPoints() {
		return points;
	}


	public boolean isPoint(Position pos) {
		return (this.points.containsKey(pos));
	}

	private Point getPoint(Position pos) {
		return points.get(pos);
	}

	public boolean isEmptyPoint(Position pos) {
		return (this.getPoint(pos).getStone() == Stone.EMPTY);
	}

	public void setPoint(Position pos, Stone s) {
		points.put(pos, new Point(s));
	}

	public int getBlackScore() {
		return blackScore;
	}

	public int getWhiteScore() {
		return whiteScore;
	}

	/**
	 * Empties a position / removes a stone.
	 * @param pos
	 */
	public void removePoint(Position pos) {
		points.put(pos, new Point(Stone.EMPTY));
	}

	/**
	 * Returns a set of 2 (corner), 3 (edge) or 4 (center) neighbours of a specific position
	 * @param pos
	 * @return set
	 */
	private Set<Position> getNeighbours(Position pos){
		Set<Position> neighbours = new HashSet<>();

		for (int i = pos.getX() - 1; i <= pos.getX() + 1; i++) {
			Position a = new Position(i, pos.getY());
			if (isPoint(a))	neighbours.add(a);
		}
		for (int i = pos.getY() - 1; i <= pos.getY() + 1; i++) {
			Position a = new Position(pos.getX(), i);
			if (isPoint(a))	neighbours.add(a);
		}
		return neighbours;
	}


	/**
	 * Returns all empty positions surrounding argument position.
	 * @param cluster
	 * @return set
	 */
	private Set<Position> encapsulatedBy(Set<Position> cluster, Stone stone) {
		Set<Position> freePositions = new HashSet<>();

		for(Position clusterPos : cluster){
			for (Position p : getNeighbours(clusterPos)) {
				if (getPoint(p).getStone() == stone) {
					freePositions.add(p);
				}
			}
		}
		return freePositions;
	}

	/**
	 * Returns a cluster of defending stone positions in which position pos is situated.
	 * @param pos
	 * @return set
	 */
	public Set<Position> defendingCluster(Position pos) {
		Stone defend = getPoint(pos).getStone();
		Set<Position> defendingCluster = new HashSet<>();
		Set<Position> temp = new HashSet<>();

		defendingCluster.add(pos);

		while (temp.size() != defendingCluster.size()){
			for (Position r : defendingCluster) temp.add(r);
			for (Position p : temp) {
				for (Position q : getNeighbours(p)) {
					if(isPoint(q) && getPoint(q).getStone() == defend){
						defendingCluster.add(q);
					}
				}
			}
		}
		return defendingCluster;
	}


	/**
	 * Returns all liberty positions surrounding argument position (even if arguments exists in cluster of samecolor stones).
	 * @param pos
	 * @return set
	 */
	private Set<Position> surroundingStones(Position pos, Stone stone) {
		return encapsulatedBy(defendingCluster(pos), stone);
	}

	/**
	 * Returns number of liberties of argument (even if arguments exists in cluster of samecolor stones).
	 * @param pos
	 * @return int
	 */
	public int numberOfLiberties(Position pos) {
		return surroundingStones(pos, Stone.EMPTY).size();
	}


	/**
	 * checks if the placement of a stone on pos is legal
	 * stone is placed outside of the dimensions of the board
	 * stone is placed on an occupied spot (black, white)
	 * @param x
	 * @oaran y
	 * @return boolean
	 */
	public boolean isAllowed(int x, int y) {
		if (isPoint(new Position(x, y)) && isEmptyPoint(new Position(x, y))) {
			return true;
		}
		System.out.println("Move not allowed: position does not exist on this playing board, or the position is not Empty.");
		return false;
	}


	/**
	 * counts the endscore on the board.
	 * int[0] = score Stone.BLACK
	 * int[1] = score Stone.WHITE
	 * territory count has been removed because it caused errors.
	 */
	public void countScore() {
		Set<Position> whiteTerritory = new HashSet<>();
		Set<Position> blackTerritory = new HashSet<>();

		for (Position p : points.keySet()) {
			switch (points.get(p).getStone()) {
				case EMPTY:

					int blackEnc = encapsulatedBy(defendingCluster(p), Stone.BLACK).size();
					int whiteEnc = encapsulatedBy(defendingCluster(p), Stone.WHITE).size();

					if(blackEnc > 0 && whiteEnc == 0) {
						blackTerritory.add(p);
					}

					if(whiteEnc > 0 && blackEnc == 0) {
						whiteTerritory.add(p);
					}
					break;
				case BLACK:
					blackTerritory.add(p);
					break;
				case WHITE:
					whiteTerritory.add(p);
					break;
			}
		}

		blackScore = blackTerritory.size();
		whiteScore = whiteTerritory.size();
	}

	/**
	 * prints a string that used for creating board history
	 * @return String
	 */
	public String toSimpleString() {
		String boardString = "";
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++)
				boardString = boardString + getPoint(new Position(i, j)).getStone().toTUIString();
		}
		return boardString;
	}

	/**
	 * Prints a GTUI
	 * @return String
	 */
	public String toString() {
		String s = "  ";
		for (int i = 0; i < dim; i++) {
			s = s + i + " ";
		}
		s = s + "\n";
		for (int i = 0; i < dim; i++) {
			String row = "" + i;
			for (int j = 0; j < dim; j++) {
				row = row + " " + getPoint(new Position(i, j)).getStone().toTUIString();
			}
			s = s + row;
			if (i < dim) {
				s = s + "\n";
			}
		}
		return s;
	}

}
