package com.bilalekrem.ruddergame.game;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import com.bilalekrem.ruddergame.util.*;
import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

import java.util.*;

public class RudderGameTest {

    static Game game;

    @Before
    public void setup() {
        game = new RudderGame();
    }

    @Test
    public void testInitiliazeBoard() {
        game.initiliazeBoard();

        assertNotEquals(null, game.board);

        assertEquals(33, game.board.getVertices().size());
    }
    
    @Test
    public void testInitiliazeGamePlayers() {
        Player p1 = new Player();
        p1.ID=1;
        p1.name="Foo";
        
        Player p2 = new Player();
        p2.ID=2;
        p2.name="Bar";

        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        assertEquals(game.players.contains(p1), true);
        assertEquals(game.players.contains(p2), true);

        assertNotEquals(null, p1.pieces);
        assertNotEquals(null, p2.pieces);
    }

    @Test
    public void testAttachedPiece() throws NoSuchNodeException {
        Player p1 = new Player();
        p1.ID=1;
        p1.name="Foo";
        
        Player p2 = new Player();
        p2.ID=2;
        p2.name="Bar";

        game.initiliazeBoard();
        Set<Location> a = game.board.getVertices().keySet();
        game.initiliazeGame(p1, p2);

        // be aware, created a RudderGameLocation instance not a Location.
        Location loc = new RudderGameLocation(Segment.A, 1);
        assertEquals(true, game.board.isNodeAvailable(loc));

        
    }
        

    
}