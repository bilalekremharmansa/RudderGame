package com.bilalekrem.ruddergame.game;

import com.bilalekrem.ruddergame.util.Graph;
import com.bilalekrem.ruddergame.util.Location;
import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Game class represent a game. It defined as abstract so 
 * different game can be built from this class.
 * 
 * @author Bilal Ekrem Harmansa
 */
public abstract class Game {
    public static final int LEVEL = 4;

    int ID;
    Set<Player> players; // playerId -> Player
    Graph board;
    List<Move> moves;

    protected Set<Location> locations;

    /**
     * Default constructor, constructs collections
     */
    public Game() {
        players = new HashSet<>();
        moves = new ArrayList<>();
        locations = new HashSet<>();
    }

    /** 
     * Each game can build a game board in its way. To make
     * that happen this method marked as abstract.
     */
    abstract Graph initiliazeBoard();

    /** 
     * It should call after initiliazeGame(). When board is ready
     * each piece can set up on the places on board and other 
     * preparation before game starts.
     *
     */
    abstract void initiliazeGame(Player... players);

    /**
     * Each game can be different ends. As Rudder Game rule 
     * if a player has just 3 pieces, the player is defeated.
     * As Dame game rule, if a player has no more piece on a 
     * board, the player is defeated.
     * 
     */
    abstract boolean isDefeated(Player player);

    /**
     * Basicly, Can a player make a move from
     * @param current to
     * @param target 
     */
    abstract MoveType canMove(Player player, Location current, Location target);

    /**
     * @param player makes a move from @param current to @param target.
     * 
     * @return Move object that contains information about this move. No matter that
     * move is valid or not valid. If move is not valid, move.type equals to 
     * MoveType.NONE. 
     */
    abstract Move move(Player player, Location current, Location target);

    /**
     * Move class represents players moves in a game.
     * 
     * @author Bilal Ekrem Harmansa
     */
    public static enum MoveType {
        MOVE, CAPTURE, NONE, MANDATORY_EXIST
    }
    public class Move {
        int doerID; // the player who does the move
        Location previous; // constructed segment-level
        Location current; // constructed segment-level
        MoveType type;

        public Move doer(int ID){
            this.doerID = ID;
            return this;
        }

        public Move previous(Location previous){
            this.previous = previous;
            return this;
        }

        public Move current(Location current){
            this.current = current;
            return this;
        }

        public Move type(MoveType type){
            this.type = type;
            return this;
        }

        @Override
        public String toString() {
            String playerName = players.stream().filter( (p) -> p.ID == doerID).findFirst().map( (p) -> p.name).orElse("Unknown player");
            return playerName + "moved a piece: " + 
                    previous.toString() + "-->" + current.toString();
        }
    }

    /**k
     * Pieces are movable objects deployed on a game. For instance, a pawn
     * in chess. 
     * The pieces that belong to each player are distinguished by color.
     * For this purpose created a enum Type, i.e. light piece, dark piece.
     * 
     * @author Bilal Ekrem Harmansa
     */
    public static enum PieceType {
        LIGHT, DARK
    }

    public class Piece {
        
        protected Location location; // piece' location on the graph as pair of segment-level
        PieceType type;
        
        public Piece(Location location, PieceType type) {
            this.location = location;
            this.type = type;
        }
        
        /**
         * @return the location
         */
        public Location getLocation() {
            return location;
        }

        /**
         * @param location the location to set
         */
        public void setLocation(Location location) {
            try {
                Location previousLocation = this.location;
                this.location = location;
                board.attachPiece(this, previousLocation);
            } catch(NoSuchNodeException ex) {

            }
            
        }
    }

}