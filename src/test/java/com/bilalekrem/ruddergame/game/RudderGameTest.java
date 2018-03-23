package com.bilalekrem.ruddergame.game;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import com.bilalekrem.ruddergame.game.Game.MoveType;
import com.bilalekrem.ruddergame.game.Game.Piece;
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
                
        // be aware, created a RudderGameLocation instance not a Location.
        Location loc = new RudderGameLocation(Segment.A, 1);
        assertEquals(true, game.board.isNodeAvailable(loc));

        game.initiliazeGame(p1, p2);

        assertEquals(false, game.board.isNodeAvailable(loc));
    }

    @Test
    public void testCanMove() throws NoSuchNodeException{
        Player p1 = new Player();
        p1.ID=1;
        p1.name="Foo";
        
        Player p2 = new Player();
        p2.ID=2;
        p2.name="Bar";

        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        Location loccenter = new RudderGameLocation(Segment.CENTER, 1);

        Location loca1 = new RudderGameLocation(Segment.A, 1);
        Location loca2 = new RudderGameLocation(Segment.A, 2);
        Location loca3 = new RudderGameLocation(Segment.A, 3);
        
        Location locb1 = new RudderGameLocation(Segment.B, 1);
        Location locb2 = new RudderGameLocation(Segment.B, 2);
        Location locb3 = new RudderGameLocation(Segment.B, 3);

        Location locc1 = new RudderGameLocation(Segment.C, 1);
        Location locc2 = new RudderGameLocation(Segment.C, 2);
        Location locc3 = new RudderGameLocation(Segment.C, 3);

        Location loce1 = new RudderGameLocation(Segment.E, 1);
        
        Location locf1 = new RudderGameLocation(Segment.F, 1);

        // level 1 to center
        assertEquals(MoveType.MOVE, game.canMove(p1, loca1, loccenter));

        // todo: move method is not implemented yet. Change with move when is ready.
        // level 1 to center is not acceptable.
        Piece piece = p1.pieces.stream().filter( (p) -> p.location.equals(loca1)).findFirst().get();
        piece.location = loccenter;
        game.board.attachPiece(piece , loca1); //move: loca1 to loccenter

        assertEquals(MoveType.NONE, game.canMove(p1, loca1, loccenter));

        // same level different segment
        piece = p1.pieces.stream().filter( (p) -> p.location.equals(locb1)).findFirst().get();
        piece.location = loca1;
        game.board.attachPiece(piece , locb1); //move: locb1 to loca1

        assertEquals(MoveType.MOVE, game.canMove(p1, loca1, locb1));

        // b1 is free for now, canMove b2 to b1 ?
        assertEquals(MoveType.MOVE, game.canMove(p1, locb2, locb1));

        //Can we move opponents piece ? f1 to b1
        assertEquals(MoveType.NONE, game.canMove(p1, locf1, locb1));

        // Let's move a1 to b1. Now, can move e1 to b1 with player 2
        // Do not forget, we putted a p1's piece to center, it still there.
        piece = p1.pieces.stream().filter( (p) -> p.location.equals(loca1)).findFirst().get();
        piece.location = locb1;
        game.board.attachPiece(piece , loca1); //move: a1 to b1
        assertEquals(MoveType.CAPTURE, game.canMove(p2, loce1, loca1));


    }
        

    
}