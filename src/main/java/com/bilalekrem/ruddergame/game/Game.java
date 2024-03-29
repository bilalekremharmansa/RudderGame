package com.bilalekrem.ruddergame.game;

import com.bilalekrem.ruddergame.game.Game.Move.MoveType;
import com.bilalekrem.ruddergame.util.Graph;
import com.bilalekrem.ruddergame.util.Location;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Game class represent a game. It defined as abstract so 
 * different game can be built from this class.
 * 
 * @author Bilal Ekrem Harmansa
 */
public abstract class Game {
    int ID;
    List<Player> players; // playerId -> Player
    Graph board;
    List<Move> moves;

    protected Map<Location, Piece> pieces;

    public enum GameType { 
        RUDDER, BLABLA
    }

    /**
     * Default constructor, constructs collections
     */
    protected Game() {
        players = new ArrayList<>();
        moves = new ArrayList<>();
        pieces = new HashMap<>();
    }

    /** 
     * Each game can build a game board in its way. To make
     * that happen this method marked as abstract.
     */
    abstract public Graph initiliazeBoard();

    /** 
     * It should call after initiliazeGame(). When board is ready
     * each piece can set up on the places on board and other 
     * preparation before game starts.
     *
     */
    abstract public void initiliazeGame(Player... players);

  
    /**
     * Each game can be different ends. As Rudder Game rule 
     * if a player has just 3 pieces, the player is defeated.
     * As Dame game rule, if a player has no more piece on a 
     * board, the player is defeated.
     * 
     */
    abstract protected boolean isDefeated(Player player);

    /**
     * This method determines the move type. Like, Can this move happen ?
     * What will happen when this Move is done ? Just a move ? Or does it
     * capture ?
     * 
     * @param move the move object will be determined.
     * 
     * @return returns move type of given move object as parameter.
     */
    abstract public MoveType determineMoveType(Move move);

    /**
     * move(Move) method performs the necessary operations when user make a
     * Move.
     * 
     * @param move the move object will be performed.
     * 
     * @return result of this operation, If this Move is valid and made succesfully
     * returns true, otherwise returns false. 
     */
    abstract public boolean move(Move move);

    /**
     * After a player makes a move, if he does not capture opponents one of the piece
     * the turn goes to next player. 
     * We expected from this method that 
     * @return which player's turn to make a move ?
     */
    abstract public Player activePlayer();

    /**
     * @return Assigned all locations in the Game.
     */
    public Set<Location> getLocations() {
        Set<Location> locs = board.getVertices().keySet();
        return Collections.unmodifiableSet(locs);
    }

     /**
     * @return unmodiable player list.
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Move class represents players moves in a game 'from' one place 'to' another.
     * 
     * Move class modified as static class. Jackson binder could not create a Move object
     * without a Game instance. This is the rule of Java, non static class only can
     * instantinate from outer class instance. However, I think that did not affect
     * at all. Move does not have to use Game class fields so changing as static do
     * not affect badly.
     * 
     * @author Bilal Ekrem Harmansa
     */
    
    public static class Move {
        public enum MoveType {
            MOVE, CAPTURE, NONE
        }
        public int doerID; // the player who does the move
        public Location from; // constructed segment-level
        public Location to; // constructed segment-level
        public Location captured; // captured opponents piece location
        public MoveType type;

        /** default consturctor */
        public Move() {

        }

        /** JSON deserialization */
        @JsonCreator
        private Move(@JsonProperty("doer") int doerID,
                     @JsonProperty("from") Location from,
                     @JsonProperty("to") Location to,
                     @JsonProperty("captured") Location captured,
                     @JsonProperty("moveType") MoveType type) {
            this.doerID = doerID;
            this.from = from;
            this.to = to;
            this.captured = captured;
            this.type =type;
        }

        public Move doer(int ID){
            this.doerID = ID;
            return this;
        }

        public Move from(Location from){
            this.from = from;
            return this;
        }

        public Move to(Location to){
            this.to = to;
            return this;
        }

        public Move captured(Location captured){
            this.captured = captured;
            return this;
        }

        public Move type(MoveType type){
            this.type = type;
            return this;
        }

        // if there is a value assigned to type, that means this is valid move.
        public boolean validate() {
            return type != null;
        }

        @Override
        public String toString() {
            return doerID + "moved a piece: " + from.toString() + "-->" + to.toString();
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
        protected PieceType type;
        
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
            Location previousLocation = this.location;
            if(previousLocation != null) pieces.put(previousLocation, null);
            this.location = location;
            pieces.put(location, this);
        }

        /**
         * @return the type of Piece
         */
        public PieceType getType() {
            return this.type;
        }

        @Override
        public String toString(){
            return type.name() + " piece at " + location.toString();
        }
    }

}