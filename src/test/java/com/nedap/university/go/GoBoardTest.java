package com.nedap.university.go;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.model.Position;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

/**
 * TESTS
 * @author martijn.slot
 *
 */
public class GoBoardTest {

	private Game game;

	@Before
	public void setUp() {
		game = new Game(5);

	}
	
	@Test
	public void testIsPoint() {
		assertTrue(game.getBoard().isPoint(new Position(1,1)));
	}

    @Test
    public void testisAllowed() {
        initializeBaseBoardOne();
        assertTrue(game.getBoard().isAllowed(3, 3));
        assertFalse(game.getBoard().isAllowed(2, 2));
    }
	
	@Test
	public void testCluster() {
		game.doMove(0,0);
		game.doMove(2,0);
		game.doMove(1,0);
		game.doMove(2,1);
		game.doMove(0,1);
		game.doMove(2,2);
		game.doMove(1,1);
		game.doMove(1,2);
		game.doMove(4,4);
		Set<Position> a = game.getBoard().defendingCluster(new Position(0,0));
		assertTrue(a.contains(new Position(1,1)));
		assertTrue(a.contains(new Position(0,1)));
        assertTrue(a.contains(new Position(1,0)));
        assertTrue(a.contains(new Position(0,0)));
        assertTrue(a.size() == 4);

	}
	
	@Test
	public void testClusterRemoval() {
        initializeBaseBoardOne();
        Set<Position> a = game.getBoard().defendingCluster(new Position(0,0));
        assertTrue(a.contains(new Position(1,1)));
        assertTrue(a.contains(new Position(0,1)));
        assertTrue(a.contains(new Position(1,0)));
        assertTrue(a.contains(new Position(0,0)));
        assertTrue(a.size() == 4);
	}

	@Test
	public void testScore() {
        initializeBaseBoardOne();
        game.countScore();
        assertTrue(game.getScores().equals("1 9"));
        initializeBaseBoardTwo();
        game.countScore();
        assertTrue(game.getScores().equals("15 5"));
	}

	@Test
    public void testTableflip() {
        game.tableflipMove("black");
        assertThat(game.getScores(), is("-1 -1"));
    }


    @Test
	public void testKO() {
	    initializeBaseBoardThree();
	    assertTrue(game.moveAllowed(1, 1));
	}

	@Test
    public void testCommands() throws IOException {
//	    DetermineCommand test = new DetermineCommand();
//	    assertThat(test.determineServerCommand("apekop", new ClientHandler(new Socket(), new GoServer(1234))), is(new ServerCrappyCommand("apekop".split(" "))));
    }

    private void initializeBaseBoardOne() {
        game.reset();
        game.doMove(0,0);
        game.doMove(2,0);
        game.doMove(1,0);
        game.doMove(2,1);
        game.doMove(0,1);
        game.doMove(2,2);
        game.doMove(1,1);
        game.doMove(1,2);
        game.doMove(4,4);
        game.doMove(0,2);
    }

    private void initializeBaseBoardTwo() {
        game.reset();
        game.doMove(2,1);
        game.doMove(0,0);
        game.doMove(2,2);
        game.doMove(1,1);
        game.doMove(2,3);
        game.doMove(0,4);
        game.doMove(2,4);
        game.doMove(0,2);
        game.doMove(2,0);
    }
    private void initializeBaseBoardThree() {
        game.reset();
        game.doMove(0,1);
        game.doMove(0,2);
        game.doMove(1,0);
        game.doMove(1,1);
        game.doMove(2,1);
        game.doMove(1,3);
        game.doMove(2,4);
        game.doMove(2,2);
        game.doMove(1,2);
    }
}
