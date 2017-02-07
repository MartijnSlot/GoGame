package com.nedap.university.go;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.model.Player;
import com.nedap.university.go.model.Position;
import com.nedap.university.go.model.Stone;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

/**
 * TESTS, obsolete and does not work because the entire game is not working
 * @author martijn.slot
 *
 */
public class KoTest {

	private Game game;
	private Player player1 = new Player("jan", Stone.BLACK);
	private Player player2 = new Player("piet", Stone.WHITE);

	

	@Before
	public void setUp() {
//		game = new Game(player1, player2, 5);

	}
	
	@Test
	public void testIsPoint() {
		assertTrue(game.getBoard().isPoint(new Position(1,1)));
	}
	
	@Test
	public void testCluster() {
		player2.makeMove(game.getBoard(), new Position(2,1));
		player2.makeMove(game.getBoard(), new Position(1,2));
		player2.makeMove(game.getBoard(), new Position(3,2));
		player2.makeMove(game.getBoard(), new Position(2,3));
		player2.makeMove(game.getBoard(), new Position(3,1));
		player2.makeMove(game.getBoard(), new Position(5,3));
		player2.makeMove(game.getBoard(), new Position(3,2));	
		player1.makeMove(game.getBoard(), new Position(1,3));
		player1.makeMove(game.getBoard(), new Position(2,4));
		player1.makeMove(game.getBoard(), new Position(3,3));
		player1.makeMove(game.getBoard(), new Position(2,2));
		player1.makeMove(game.getBoard(), new Position(5,2));
		player1.makeMove(game.getBoard(), new Position(1,1));
		player1.makeMove(game.getBoard(), new Position(1,2));
		player1.makeMove(game.getBoard(), new Position(2,2));
		Set<Position> a = game.getBoard().defendingCluster(new Position(1,1));
		assertTrue(a.contains(new Position(1,2)));
		assertTrue(a.contains(new Position(2,2)));
	}
	
	@Test
	public void testEmpty() {
		game.reset();
		assertTrue(game.getBoard().isEmptyPoint(new Position(5,5)));
		assertTrue(game.getBoard().isEmptyPoint(new Position(4,4)));
		assertTrue(game.getBoard().isEmptyPoint(new Position(4,5)));

	}

	@Test
	public void testRemoval() {
		assertTrue(game.getBoard().isEmptyPoint(new Position(1,1)));
		assertTrue(game.getBoard().isEmptyPoint(new Position(1,2)));
		assertTrue(game.getBoard().isEmptyPoint(new Position(2,2)));

	}
	
	@Test
	public void testKO() throws IOException {
//		player2.makeMove(game.getBoard(), game.checkPos("1,3"));
//		game.writeHistory();
//		player2.makeMove(game.getBoard(), game.checkPos("2,1"));
//		game.writeHistory();
//		player2.makeMove(game.getBoard(), game.checkPos("2,2"));
//		game.writeHistory();
//		player2.makeMove(game.getBoard(), game.checkPos("1,2"));
//		game.writeHistory();
//		player2.makeMove(game.getBoard(), game.checkPos("1,1"));
//		game.writeHistory();
//		game.updateTUI();
//		player2.makeMove(game.getBoard(), game.checkPos("1,2"));
//		game.updateTUI();

		
		assertTrue(game.getBoard().isEmptyPoint(new Position(1,2)));

	}
}
