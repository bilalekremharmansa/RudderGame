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

    static RudderGame game;

    Player p1, p2;

    Location loccenter, loca1, loca2, loca3, 
        locb1, locb2, locb3, locb4,
        locc1, locc2, locc3, locc4,
        locd1, 
        loce1, 
        locf1,
        locg1;

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
        locg1 = new RudderGameLocation(Segment.G, 1);
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
    public void testDetermineMoveType() throws NoSuchNodeException{
        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        // level 1 to center, now, p2's turn
        Move move1 = game.new Move().doer(p1.ID).from(loca1).to(loccenter); 
        assertEquals(MoveType.MOVE, game.determineMoveType(move1));

        // level 1 to center is not acceptable
        game.move(move1);
        assertEquals(MoveType.NONE, game.determineMoveType(move1));

        // player 2 make a move e1 to a1, p1's piece is captured. 
        Move move2 = game.new Move().doer(p2.ID).from(loce1).to(loca1); 
        assertEquals(MoveType.CAPTURE, game.determineMoveType(move2));
        game.move(move2);
        assertEquals(MoveType.NONE, game.determineMoveType(move2));
        assertEquals(true, game.board.isNodeAvailable(loccenter));
        
        // p1's turn, there is a mandatory move, it must be a2 to center
        // but we first try b1 to center. However we will not be able to move
        // as you know we test determineMoveType method so we dont care
        Move move3 = game.new Move().doer(p1.ID).from(locb1).to(loccenter); 
        assertEquals(MoveType.MOVE, game.determineMoveType(move3));

        // still p1's turn
        Move move4 = game.new Move().doer(p1.ID).from(loca2).to(loccenter); 
        assertEquals(MoveType.MOVE, game.determineMoveType(move3));
    }

    @Test
    public void testMove() throws NoSuchNodeException {
        game.initiliazeBoard();
        game.initiliazeGame(p1, p2);

        // move a1 to center by p1
        Move move1 = game.new Move().doer(p1.ID).from(loca1).to(loccenter); 
        boolean result1 = game.move(move1);
        assertEquals(true, result1);

        // move a1 to center by p1, which there is no Piece
        // at a1 anymore
        Move move2 = game.new Move().doer(p1.ID).from(loca1).to(loccenter); 
        boolean result2 = game.move(move2);
        assertEquals(false, result2);

        // move e1 to a1 by p1, p1 is not owner of e1 and p2's turn now.
        // p1 can not make a move.
        Move move3 = game.new Move().doer(p1.ID).from(loce1).to(loca1); 
        boolean result3 = game.move(move3);
        assertEquals(false, result3);
        assertEquals(false, game.checkMandatoryFlag());

        // move e1 to a1 by p2, valid move
        Move move4 = game.new Move().doer(p2.ID).from(loce1).to(loca1); 
        boolean result4 = game.move(move4);
        assertEquals(true, result4);
        // only assings if result is true
        assertEquals(MoveType.CAPTURE, move4.type); 
        assertEquals(false, game.checkMandatoryFlag());
        Piece p = game.board.getAttachedPiece(loca1);

        // try to move b2 to center(Type.MOVE), but there is mandatory
        // a2 to center
        Move move5 = game.new Move().doer(p1.ID).from(locb2).to(loccenter); 
        boolean result5 = game.move(move5);
        assertEquals(false, result5);
        assertEquals(null, move5.type);
        assertEquals(true, game.checkMandatoryFlag());

        // if p1 move a2 to center(this is mandatory move), then
        // p2 move f1 to e1, then
        // p1 move a3 to a2
        // now p2's turn. p2 move e1 to a1(captured center), and p2 STILL
        // can make a move. Because p2 can capture one more piece by moving
        // a1 to a3. Lets see.
        Move move6 = game.new Move().doer(p1.ID).from(loca2).to(loccenter); 
        boolean result6 = game.move(move6);
        Move move7 = game.new Move().doer(p2.ID).from(locf1).to(loce1); 
        boolean result7 = game.move(move7);
        Move move8 = game.new Move().doer(p1.ID).from(locd1).to(locf1); 
        boolean result8 = game.move(move8);
        Move move9 = game.new Move().doer(p2.ID).from(locg1).to(loce1); 
        boolean result9 = game.move(move9);
        Move move10 = game.new Move().doer(p2.ID).from(loce1).to(loca1); 
        boolean result10 = game.move(move10);
        assertEquals(true, result6);
        assertEquals(true, result7);
        assertEquals(true, result8);
        assertEquals(true, result9);
        assertEquals(true, result10);
    }
        

    
}