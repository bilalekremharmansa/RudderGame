package com.bilalekrem.ruddergame.game;

import com.bilalekrem.ruddergame.util.Graph;
import com.bilalekrem.ruddergame.util.Segment;
import com.bilalekrem.ruddergame.util.Location;
import com.bilalekrem.ruddergame.util.RudderGameLocation;
import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.Set;
import java.util.Collections;
import java.util.LinkedList;

public class RudderGame extends Game{

    private static final Logger LOGGER = LogManager.getLogger(RudderGame.class);

    public static final int LEVEL = 4;
    private Queue<Location> capturedPieces;

    /**
     * Players who plays the game are in a sequence list, players.
     * this field, turn, indicates which players' turn is in current time.
     */
    protected int turn;

    /**
     * This field can be changed in move. If move(Move) fails and returns false
     * because of existing of mantary move and user is trying to do just a 
     * MoveType.MOVE then this flag sets.
     */
    private boolean MANDATORY_MOVE_EXIST = false;

    public RudderGame() {
        capturedPieces = new LinkedList<>(); // forgot assigning
    }
    
	@Override
	public Graph initiliazeBoard() {
        board = new Graph();
        
        Segment[] segments = Segment.values();
        /** segments.length minus 1 not including CENTER */
        Location[][] locations = new Location[segments.length-1][];

        /** either, i dont like to write lines below. */
        Location center = new RudderGameLocation(Segment.CENTER, 0);

        int index = 0;
        for (Segment segment: segments) {
            if(segment == Segment.CENTER) continue;

            Location[] locs = new Location[RudderGame.LEVEL];
            for (int j = 0; j < RudderGame.LEVEL; j++) {
                Location loc = new RudderGameLocation(segment, j+1);
                locs[j] = loc;
            }
            locations[index++] = locs;
        }

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
         * The loop below build link between CENTER and Segments which 
         * Segments level are 1(locs[0])
         */
        for (Location[] locs : locations) {
            try {
                board.addEdge(center, locs[0]);
            }catch(NoSuchNodeException ex){
                LOGGER.error(ex.getMessage());
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
            /** I know that Segments.values() are in order alphabeticly. 
             *  locations[0] contains Segment A
             *  locations[7] contains Segment H
             */
            board.addEdge(locations[0][0], locations[7][0]);
            board.addEdge(locations[0][1], locations[7][1]);
            board.addEdge(locations[0][2], locations[7][2]);
            board.addEdge(locations[0][3], locations[7][3]);
        }catch(NoSuchNodeException ex){
            LOGGER.error(ex.getMessage());
        }   
       
               
        return board;
    }
    
    @Override
	public void initiliazeGame(Player... players) {
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

        // Segments A to H not included CENTER.
        Segment[] segments = new Segment[]{
            Segment.A, Segment.B, Segment.C, Segment.D,
            Segment.E, Segment.F, Segment.G, Segment.H};
        int index = 0;

        // playerOne pieces're placed on Segments A,B,C,D
        for (int i = 0; i < segments.length / 2; i++) {
            for (int j = 1; j <= LEVEL; j++) {
                Location location = new RudderGameLocation(segments[index], j);
                Piece piece = new Piece(location, playerOne.pieceType);
    
                pieces.put(location, piece); // first call, no prev location
                playerOne.pieces.add(piece);

            }
            index++;
        }

        // playerTwo pieces're placed on Segments E,F,G,H
        for (int i = 0; i < segments.length / 2; i++) {
            for (int j = 1; j <= LEVEL; j++) {
                Location location = new RudderGameLocation(segments[index], j);
                Piece piece = new Piece(location, playerTwo.pieceType);
    
                pieces.put(location, piece); // first call, no prev location
                playerTwo.pieces.add(piece);            
            }
            index++;
        }
    }

	@Override
	public boolean isDefeated(Player player) {
		return player.pieces.size()<=3;
	}

	@Override
	public MoveType determineMoveType(Move move) {
        Player player = players.stream().filter((p) -> p.ID ==move.doerID).findFirst().orElse(null);
        if(player == null) return MoveType.NONE; // player not exist.
        else if (player != activePlayer()) return MoveType.NONE; // is player's turn ?
        
        Location current = move.from; // current location
        Location target = move.to; // target location

        // if user has a Piece at current
        long currentExist = player.pieces.stream().filter( (piece) -> piece.location.equals(current) ).count();
        if(currentExist<1) {
            LOGGER.info("Player {} does not have a Piece on {}", player.name, current);
            return MoveType.NONE;
        }

        try {
            // Check is target empty or available to move ?
            boolean isNodeAvailable = pieces.get(target) == null ? true : false;

            if(isNodeAvailable) {
                // If target node and current node are neighbour.
                boolean contains = board.getAdjacencies(current).contains(target);
                if(contains) {
                    return MoveType.MOVE;
                }

                // Can @param player capture by jumping over opponent pieces ? 
                Location between = getBetween(current, target);
                /**
                 * Bug fixed, the code was trying to capture a piece that not exist.
                 * getBetween(..) returns location between current and target. However, I've never checked
                 * that is there any Piece at locationBetween ? If there is not. There will be no 
                 * captured pieces. 
                 */
                if(between != null) {
                    Piece pieceBetweenLocation = pieces.get(between);
                    // bug fixing, before adding a captured piece, clear data from previous usage.
                    capturedPieces.clear(); 
                    if(pieceBetweenLocation != null && pieceBetweenLocation.type != player.pieceType) {
                        capturedPieces.add(between);
                        return MoveType.CAPTURE;
                    }
                }
            }
        } catch(NoSuchNodeException ex ) {
            LOGGER.error("There is no vertex as {}", target);
        }
		return MoveType.NONE;
    }
    
    /**
     * This method returns a Location if a Location exists between @param firstLocation
     * and @param secondLocation, if there is no valid Location returns null.
     * 
     * Does this code smells ?
     */
    private Location getBetween(Location firstLocation, Location secondLocation) {
        /** 
         * Location that between two locations can be found like 
         * -get first location' neighbours 
         * -get second location' neighbours. 
         * -Compare those and find the common elements.
         * 
         * common elements in two collection: stackoverflow.com/a/5943349/5929406
         * 
         * However, we have a special case, if founded location is CENTER. In that case, there
         * must be a straight line from firstLocation to secondLocation.
         */
        if(firstLocation.equals(secondLocation)) return null;
        
        try {
            Set<Location> neighbour = board.getAdjacencies(firstLocation);
            Set<Location> secondNeighbour = board.getAdjacencies(secondLocation);   
            neighbour.retainAll(secondNeighbour);

            for (Location loc : neighbour) {
                /**
                 * If statements below are not written like if-else if-else if,
                 * First of all, there is no point to that. Each if condition comes true, code 
                 * returns loc and do nothing else in this method. Otherwise, need to check other
                 * conditions as well so we just keep it like that.
                 * Secondly, if we do that, if-elseif-elseif, secondCondition and thirdCondition
                 * would've conflicted. Assume that, 
                 * player1 piece at CENTER
                 * player2 piece at E1
                 * A1 is free
                 * player2 wants to move E1 to A1. We expect that thirdCondition will take care with
                 * this case. 
                 * (loc.segment == Segment.CENTER && Math.abs(firstLocation.segment.compareTo(secondLocation.segment)) == 4)
                 * However, secondCondition takes care with this one so I've just avoided from this
                 * situtation in this way.
                 */
                if (firstLocation.segment == secondLocation.segment &&
                        loc.level == ((firstLocation.level + secondLocation.level) / 2)) {
                    return loc;
                }
                RudderGameLocation _firstLocation = (RudderGameLocation) firstLocation;
                RudderGameLocation _secondLocation = (RudderGameLocation) secondLocation;
                RudderGameLocation _loc = (RudderGameLocation) loc;
                if (firstLocation.level == secondLocation.level) {
                    /**
                     * If they are on same level.
                     * String comparison to determine that if firstLocation and secondLocation 
                     * make a straight line ? If distance is 1 
                     * i.e. A-B, B-C,....,H-A
                     */
                    if (_firstLocation.segmentDistance(_loc) == 1 && _loc.segmentDistance(_secondLocation) == 1) {
                        return loc;
                    }
                }
                if(loc.segment == Segment.CENTER && _firstLocation.segmentDistance(_secondLocation) == 4) {
                     /**
                     * As we know a Segment can be A,B,C D,E,F,G,H and we know that there is a 
                     * straight line from A to E, B to F, D to H and etc. We can use String comparison to
                     * determine that if firstLocation and secondLocation make a straight
                     * line ? If segmentDistance is 4 that menas they are neighbour.
                     * 
                     * Otherwise, loc == Segment.CENTER is not acceptable.
                     */
                    return loc;
                }
                if (firstLocation.segment == Segment.CENTER && secondLocation.level == 2 &&
                        secondLocation.segment == loc.segment) { 
                    //if common neighbour is not Center, we can say that this location is valid.
                    return loc;
                }
                if (secondLocation.segment == Segment.CENTER && firstLocation.level == 2 &&
                        firstLocation.segment == loc.segment) { 
                    //if common neighbour is not Center, we can say that this location is valid.
                    return loc;
                }
                
            }

        } catch (NoSuchNodeException e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    /**
     * Addition information about this method, if there is a mandatory move and user
     * is doing that something lower priority. This methods sets MANDATORY_MOVE_EXIST flag sets.
     */
	@Override
	public boolean move(Move move) {
        MoveType moveType = determineMoveType(move);

        Player player = players.stream().filter((p) -> p.ID ==move.doerID).findFirst().orElse(null);
        if(player == null) return false; // player not exist.

        Location current = move.from; // current location
        Location target = move.to; // target location

        MANDATORY_MOVE_EXIST = false; // reset mandatory flag.

        //there was a bug the lines below, fixed in a way.
        if(moveType == MoveType.NONE) {
            if(mandatoryMove(player)){
                MANDATORY_MOVE_EXIST = true;
            }
            return false;
        }

        if(moveType == MoveType.MOVE) {
            if(mandatoryMove(player)){
                MANDATORY_MOVE_EXIST = true;
                return false;
            }
        }
        
        // Moving process
        for (Piece piece : player.pieces) {
            if(piece.location.equals(current)){
                piece.setLocation(target);; // piece's new location is target
            }
        }

        if(moveType == MoveType.CAPTURE) {
            /**
             *  
             * The code below, finds the owner of captured piece, say it Player opponent.
             * then removes captured piece in opponents pieces Collection.
             * 
             * Do I know nothing about functional programming or the code below is
             * just normal ? It looks extremely complexity. I need to give a instruction 
             * of algorithm I used here.
             * 
             * 1- Iterate capturedPieces
             * 2- Find the owner of each captured piece.
             * 3- If can not find the owner do nothing
             * 4- If found(always will be), get Piece information from Graph.
             * 5- Remove the Piece in player p.pieces which p is owner of captured Piece.
             * 6- invoke breakAttach(capturedLocation) to break connection between the 
             * Node object and Piece object.
             */
            
            capturedPieces.stream().forEach( (captured)->{
                players.stream().
                    filter((opponent) ->opponent.pieces.stream().
                        filter((piece)->piece.location.equals(captured)).findFirst().isPresent()).
                        findFirst().ifPresent((opponent)-> {
                            Piece toBeRemovedPiece = pieces.get(captured);
                            pieces.put(captured, null); 
                            opponent.pieces.remove(toBeRemovedPiece);
                            
                            // If captured a piece, we had to be able to locate the piece
                            // which is captured.
                            move.captured = captured;
                        });
            });
        }

        // With this move, if one of the opponents piece captured, then player has 
        if(moveType != MoveType.CAPTURE) turn();
        else if(moveType == MoveType.CAPTURE && !capturableMoveExist(player, move.to)) {
            /**
             * At least one opponent piece is captured by player. We might have a chance to make an
             * another move when put his piece from currentLocation to target location, if player can 
             * capture at least one more opponent piece. 
             * 
             * moveType == MoveType.CAPTURE && !capturableMoveExist(player, move.to), the condition
             * tells that after the move, there is no chance to capture another piece then player 
             * do not have a chance for another move
             */

             turn();
        } // else moveType == MoveType.CAPTURE && !capturableMoveExist(player, move.to) gets true,
            // so player can make another move.

        // Just before finishing the method, dont forget the change move type. It's not assigned yet.
        move.type(moveType);
        moves.add(move);
        return true;
	}

    /**
     * As RudderGame rule, if player can capture oppenent' piece, the player 'has to'
     * capture that piece. Of course, if there are several choices(several capturing moves)
     * player is free to choose which Piece will capture. This is mandatory.
     * 
     * mandatoryMove method checks existing of mandatory move for each piece of player.
     * 
     * @param player the player who might be forced to make a move that type CAPTURE.
     * 
     * @return if there is at least one mandatory move then returns true, otherwise false.
     */
    private boolean mandatoryMove(Player player) {
        for (Piece playerPiece : player.pieces) {
            boolean exists = capturableMoveExist(player, playerPiece.location);
            if(exists) return true;
        }

        return false;
    }

    /**
     * Basicly, this method search for capturable opponent piece. 
     * @param player the player who will make the move
     * @param currentLocation one of the currentLocation of player' piece. This is start point
     * to search for.
     * 
     * This method follows the algorithm below,
     * 1- Search into pieces's neighbours.
     * 2- Iterate through neighbours - neighbour
     * 3- If there is no piece at neighbour in graph then continue
     * 4- If neighbour is a Piece of @param player then continue(we're looking opponent piece)
     * 5- If neighbour is a opponent Piece then find the Opponent Piece's neighbours
     * as opponentPieceNeighbours.
     * 6- Iterate through opponentPieceNeighbours, if there is at least one location is
     * available then here we go, we found our move.
     * 
     * @return returns where do player makes a move from currentLocation to. 
     */
    private boolean capturableMoveExist(Player player, Location currentLocation) {
        try{
            Set<Location> neighbours = board.getAdjacencies(currentLocation);
            // HIIIIGH level coupling. Messed up here :/
            for (Location neighbour : neighbours) {
                // if there is no Piece at Location neigbour, then this move type is
                // MoveType.MOVE which is not what we look it for.
                Piece pieceAtLocationNeighbour = pieces.get(neighbour);
                if(pieceAtLocationNeighbour == null) continue;
                // if pieceAtLocationNeighbour is Player's piece, then continue.
                if(pieceAtLocationNeighbour.type == player.pieceType) continue;
                else { // if pieceAtLocationNeighbour is "opponet's piece"(that what we look for) then,
                    Set<Location> opponentPieceNeighbours = board.getAdjacencies(pieceAtLocationNeighbour.location);
                    // if there is a empty Location in opponentPieceNeighbours then
                    // our move can be exist!
                    for (Location locOpponentNeighbour : opponentPieceNeighbours) {
                        Move move = new Move().doer(player.ID).from(currentLocation).to(locOpponentNeighbour);
                        if(determineMoveType(move) == MoveType.CAPTURE) {
                            LOGGER.info("Mandatory move -" + currentLocation + "-" + locOpponentNeighbour);
                            return true;
                        }
                            
                    }
                }
            }
        }catch(NoSuchNodeException ex) {
            LOGGER.error(ex.getMessage());
        }
        

        return false;
    }

    @Override
    public Player activePlayer() {
        return players.get(turn);
    }

    private void turn() {
        turn = (++turn) % 2; //2 means players.size(). RudderGame always plays with 2 players.
    }
    public boolean checkMandatoryFlag() {
        return MANDATORY_MOVE_EXIST;
    }

    public Piece getPiece(Location loc) {
        return pieces.get(loc);
    }

}