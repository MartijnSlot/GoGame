package com.nedap.university.go;

import com.nedap.university.go.controller.Game;
import com.nedap.university.go.gocommands.Command;
import com.nedap.university.go.gocommands.DetermineCommand;
import com.nedap.university.go.gocommands.clientToServer.*;
import com.nedap.university.go.gocommands.serverToClient.*;
import com.nedap.university.go.model.Position;
import org.junit.Before;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.instanceOf;
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
public class GoGameTest {

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
	    assertFalse(game.moveAllowed(1, 1));
	}

	@Test
    public void testTheServerForClientCommands() throws IOException {
	    DetermineCommand test = new DetermineCommand();
	    Command command;
	    String[] message = {"APEKOP", "TABLEFLIP", "PASS", "CANCEL", "GO", "SCORE", "EXIT", "PLAYER", "CHAT"};

	    command = test.determineServerCommand(message[0], null);
	    assertThat(command, is(instanceOf(ClientCrappyCommand.class)));

        command = test.determineServerCommand(message[1], null);
        assertThat(command, is(instanceOf(TableFlipCommand.class)));

        command = test.determineServerCommand(message[2], null);
        assertThat(command, is(instanceOf(PassCommand.class)));

        command = test.determineServerCommand(message[3], null);
        assertThat(command, is(instanceOf(CancelCommand.class)));

        command = test.determineServerCommand(message[4], null);
        assertThat(command, is(instanceOf(GoCommand.class)));

        command = test.determineServerCommand(message[5], null);
        assertThat(command, is(instanceOf(ScoreCommand.class)));

        command = test.determineServerCommand(message[6], null);
        assertThat(command, is(instanceOf(ExitCommand.class)));

        command = test.determineServerCommand(message[7], null);
        assertThat(command, is(instanceOf(PlayerCommand.class)));

        command = test.determineServerCommand(message[8], null);
        assertThat(command, is(instanceOf(ClientChatCommand.class)));
    }

    @Test
    public void testTheClientForServerCommands() throws IOException {
        DetermineCommand test = new DetermineCommand();
        Command command;
        String[] message = {"APEKOP", "TABLEFLIPPED", "PASSED", "CHAT", "WARNING", "READY", "VALID", "INVALID", "END"};

        command = test.determineClientCommand(message[0], null);
        assertThat(command, is(instanceOf(ServerCrappyCommand.class)));

        command = test.determineClientCommand(message[1], null);
        assertThat(command, is(instanceOf(TableFlippedCommand.class)));

        command = test.determineClientCommand(message[2], null);
        assertThat(command, is(instanceOf(PassedCommand.class)));

        command = test.determineClientCommand(message[3], null);
        assertThat(command, is(instanceOf(ServerChatCommand.class)));

        command = test.determineClientCommand(message[4], null);
        assertThat(command, is(instanceOf(WarningCommand.class)));

        command = test.determineClientCommand(message[5], null);
        assertThat(command, is(instanceOf(ReadyCommand.class)));

        command = test.determineClientCommand(message[6], null);
        assertThat(command, is(instanceOf(ValidCommand.class)));

        command = test.determineClientCommand(message[7], null);
        assertThat(command, is(instanceOf(InvalidCommand.class)));

        command = test.determineClientCommand(message[8], null);
        assertThat(command, is(instanceOf(EndCommand.class)));
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
