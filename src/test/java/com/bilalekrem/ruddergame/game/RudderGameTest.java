package com.bilalekrem.ruddergame.game;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import com.bilalekrem.ruddergame.game.Game.*;
import com.bilalekrem.ruddergame.util.*;
import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

import java.util.*;

public class RudderGameTest {

    static Game game;

    Player p1, p2;

    Location loccenter, loca1, loca2, loca3, 
        locb1, locb2, locb3, locb4,
        locc1, locc2, locc3, locc4,
        locd1, 
        loce1, 
        locf1;

    @Before
    public void setup() {
        game = new RudderGame();

        p1 = new Player();
        p1.ID=1;
        p1.name="Foo";
        
        p2 = new Player();
        p2.ID=2;
        p2.name="Bar";

        loccenter = new RudderGameLocation(Segment.CENTER, 1);
        loca1 = new RudderGameLocation(Segment.A, 1);
        loca2 = new RudderGameLocation(Segment.A, 2);
        loca3 = new RudderGameLocation(Segment.A, 3);
        locb1 = new RudderGameLocation(Segment.B, 1);
        locb2 = new RudderGameLocation(Segment.B, 2);
        locb3 = new RudderGameLocation(Segment.B, 3);
        locc1 = new RudderGameLocation(Segment.C, 1);
        locc2 = new RudderGameLocation(Segment.C, 2);
        locc3 = new RudderGameLocation(Segment.C, 3);
        locd1 = new RudderGameLocation(Segment.D, 1);
        loce1 = new RudderGameLocation(Segment.E, 1);
        locf1 = new RudderGameLocation(Segment.F, 1);
    }

    @Test
    public void testInitiliazeBoard() {
        game.initiliazeBoard();

        assertNotEquals(null, game.board);

        assertEquals(33, game.board.getVertices().size());
    }
    
    @Test
    public void testInitiliazeGamePlayers() {
        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        assertEquals(game.players.contains(p1), true);
        assertEquals(game.players.contains(p2), true);

        assertNotEquals(null, p1.pieces);
        assertNotEquals(null, p2.pieces);
    }

    @Test
    public void testAttachedPiece() throws NoSuchNodeException {
        game.initiliazeBoard();
                
        // be aware, created a RudderGameLocation instance not a Location.
        assertEquals(true, game.board.isNodeAvailable(loca1));

        game.initiliazeGame(p1, p2);

        assertEquals(false, game.board.isNodeAvailable(loca1));
    }

    @Test
    public void testCanMove() throws NoSuchNodeException{
        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        // level 1 to center
        assertEquals(MoveType.MOVE, game.canMove(p1, loca1, loccenter));

        // todo: move method is not implemented yet. Change with move when is ready.
        // level 1 to center is not acceptable.
        game.move(p1,loca1, loccenter);
        assertEquals(MoveType.NONE, game.canMove(p1, loca1, loccenter));

        // same level different segment
        game.move(p1, locb1, loca1);
        assertEquals(MoveType.MOVE, game.canMove(p1, loca1, locb1));

        // b1 is free for now, canMove b2 to b1 ?
        assertEquals(MoveType.MOVE, game.canMove(p1, locb2, locb1));

        //Can we move opponents piece ? f1 to b1
        assertEquals(MoveType.NONE, game.canMove(p1, locf1, locb1));

        // Let's move a1 to b1. Now, can move e1 to b1 with player 2
        // Do not forget, we putted a p1's piece to center, it still there.
        game.move(p1, loca1, locb1);
        assertEquals(MoveType.CAPTURE, game.canMove(p2, loce1, loca1));
    }

    @Test
    public void testMove() throws NoSuchNodeException {
        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        // move a1 to center by p1
        Move move = game.move(p1, loca1, loccenter);
        assertEquals(MoveType.MOVE, move.type);

        // move a1 to center by p1, which there is no Piece
        // at a1 anymore
        move = game.move(p1, loca1, loccenter);
        assertEquals(MoveType.NONE, move.type);

        // move e1 to a1 by p1, p1 is not owner of e1
        move = game.move(p1, loce1, loca1);
        assertEquals(MoveType.NONE, move.type);

        // move e1 to a1 by p2, valid move
        move = game.move(p2, loce1, loca1);
        assertEquals(MoveType.CAPTURE, move.type);

        // check that p2 captured a piece from p1 which that piece
        // was at center.
        assertEquals(true, game.board.isNodeAvailable(loccenter));

        // try to move b1 to center(Type.MOVE), but there is mandatory
        // a2 to center
        move = game.move(p1, locb2, loccenter);
        assertEquals(MoveType.MANDATORY_EXIST, move.type);   
    }
        

    
}