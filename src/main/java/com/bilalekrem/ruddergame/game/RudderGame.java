package com.bilalekrem.ruddergame.game;

import com.bilalekrem.ruddergame.util.Graph;
import com.bilalekrem.ruddergame.util.Segment;
import com.bilalekrem.ruddergame.util.Location;
import com.bilalekrem.ruddergame.util.RudderGameLocation;
import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RudderGame extends Game{

    private static final Logger LOGGER = LogManager.getLogger(RudderGame.class);

    public RudderGame() {

    }
    
	@Override
	Graph initiliazeBoard() {
        board = new Graph();
        
        /** either, i dont like to write lines below. */
        Location center = new RudderGameLocation(Segment.CENTER, 0);

        Location[] as = new Location[]{
            new RudderGameLocation(Segment.A, 1),
            new RudderGameLocation(Segment.A, 2),
            new RudderGameLocation(Segment.A, 3),
            new RudderGameLocation(Segment.A, 4)};
        Location[] bs = new Location[]{
            new RudderGameLocation(Segment.B, 1),
            new RudderGameLocation(Segment.B, 2),
            new RudderGameLocation(Segment.B, 3),
            new RudderGameLocation(Segment.B, 4)};
        Location[] cs = new Location[]{
            new RudderGameLocation(Segment.C, 1),
            new RudderGameLocation(Segment.C, 2),
            new RudderGameLocation(Segment.C, 3),
            new RudderGameLocation(Segment.C, 4)};
        Location[] ds = new Location[]{
            new RudderGameLocation(Segment.D, 1),
            new RudderGameLocation(Segment.D, 2),
            new RudderGameLocation(Segment.D, 3),
            new RudderGameLocation(Segment.D, 4)};
        Location[] es = new Location[]{
            new RudderGameLocation(Segment.E, 1),
            new RudderGameLocation(Segment.E, 2),
            new RudderGameLocation(Segment.E, 3),
            new RudderGameLocation(Segment.E, 4)};
        Location[] fs = new Location[]{
            new RudderGameLocation(Segment.F, 1),
            new RudderGameLocation(Segment.F, 2),
            new RudderGameLocation(Segment.F, 3),
            new RudderGameLocation(Segment.F, 4)};
        Location[] gs = new Location[]{
            new RudderGameLocation(Segment.G, 1),
            new RudderGameLocation(Segment.G, 2),
            new RudderGameLocation(Segment.G, 3),
            new RudderGameLocation(Segment.G, 4)};
        Location[] hs = new Location[]{
            new RudderGameLocation(Segment.H, 1),
            new RudderGameLocation(Segment.H, 2),
            new RudderGameLocation(Segment.H, 3),
            new RudderGameLocation(Segment.H, 4)};

        Location[][] locations = new Location[][]{as,bs,cs,ds,es,fs,gs,hs};

        /**
         * center is special case that is called just once.
         * The loop below, builds vertex from Locations.. 
         */
        board.addVertex(center);
        for (Location[] locs : locations) {
            for (Location loc : locs) {
                board.addVertex(loc);
            }
        }
        
        /**
         * The loop below builds link between Locations on same 'segment'
         */
        for (Location[] locs : locations) {
            for (int i = 0; i < locs.length - 1; i++) {
                Location loc1 = locs[i];
                Location loc2 = locs[i+1];

                try {
                    board.addEdge(loc1, loc2);
                }catch(NoSuchNodeException ex){
                    LOGGER.error(ex.getMessage());
                }
                
            }
        }
        
        /**
         * The loop below builds link Locations on same 'level'
         * When loop ended, there will be a connection like,
         * a1-b1-c1-d1-e1-f1-g1-h1
         * However, as you see, its not circular which our game is 
         * not like this. h1 should've connected to a1. For this
         * purpose we connects them by manually(not in the loop).
         */

        for (int i = 0; i < locations.length - 1; i++) {

            Location[] firstLocs = locations[i];
            Location[] secondLocs = locations[i+1];

            // they, both, are same length.
            for (int j = 0; j < firstLocs.length; j++) {
                try {
                    board.addEdge(firstLocs[j], secondLocs[j]);
                }catch(NoSuchNodeException ex){
                    LOGGER.error(ex.getMessage());
                }   
            }
        }
        try {
            board.addEdge(as[0], hs[0]);
            board.addEdge(as[1], hs[1]);
            board.addEdge(as[2], hs[2]);
            board.addEdge(as[3], hs[3]);
        }catch(NoSuchNodeException ex){
            LOGGER.error(ex.getMessage());
        }   
       
               
        return board;
    }
    
    @Override
	void initiliazeGame(Player... players) {
        // using hashCode as game id
        this.ID = hashCode();

        // registering players
        Player playerOne, playerTwo;

        playerOne = players[0];
        playerOne.reset();
        playerOne.pieceType = Game.PieceType.LIGHT;

        playerTwo = players[1];
        playerTwo.reset();
        playerTwo.pieceType = Game.PieceType.DARK;

        this.players.add(playerOne);
        this.players.add(playerTwo);

        // playerOne pieces're placed on Segments A,B,C,D
        Segment[] segments = Segment.values();
        int index = 0;

        for (int i = 1; i <= LEVEL; i++) {
            Location location = new Location(segments[index++], i);
            Piece piece = new Piece(location, playerOne.pieceType);

            try {
                board.attachPiece(piece, null); // first call, no prev location
                playerOne.pieces.add(piece);
            } catch (NoSuchNodeException ex) {
                LOGGER.error("Could not find vertex v {}", location);
            }
        }
        
        // playerTwo pieces're placed on Segments E,F,G,H
        for (int i = 1; i <= LEVEL; i++) {
            Location location = new Location(segments[index++], i);
            Piece piece = new Piece(location, playerTwo.pieceType);

            try {
                board.attachPiece(piece, null);
                playerTwo.pieces.add(piece);
            } catch (NoSuchNodeException ex) {
                LOGGER.error("There is no vertex as {}", location);
            }
        }
    }

	@Override
	boolean isDefeated(Player player) {
		return player.pieces.size()<=3;
	}

	@Override
	boolean canMove(Player player, Location current, Location target) {
        // if user has a Piece at current
        long currentExist = player.pieces.stream().filter( (piece) -> piece.location.equals(current) ).count();
        if(currentExist>0) {
            LOGGER.info("Player {} does not have a Piece on {}", player.name, current);
            return false;
        }

        // checking target
        try {
            // if target node is empty
            boolean isNodeAvailable = board.isNodeAvailable(target);
            if(isNodeAvailable) {
                // if target node and current node are adjoints
                boolean contains = board.getAdjacencies(current).contains(target);
                if(contains) {
                    return true;
                }
            }
        } catch(NoSuchNodeException ex ) {
            LOGGER.error("There is no vertex as {}", target);
            return false;
        }

		return false;
	}

	@Override
	boolean move(Player player, Location current, Location target) {
		return false;
	}



}