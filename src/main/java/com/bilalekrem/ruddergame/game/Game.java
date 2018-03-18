package com.bilalekrem.ruddergame.game;

import com.bilalekrem.ruddergame.util.Graph;

import java.util.List;
import java.util.Map;

/**
 * Game class represent a game. It defined as abstract so 
 * different game can be built from this class.
 * 
 * @author Bilal Ekrem Harmansa
 */
public abstract class Game {
    public static final int LEVEL = 4;

    int ID;
    Map<Integer, Player> players; // playerId -> Player
    Graph board;
    List<Move> moves;

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
    abstract boolean canMove(Player player, String current, String target);

    /**
     * @param player makes a move from @param current to @param target.
     * 
     * @return if opponent's piece removed returns true, otherwise false.
     */
    abstract boolean move(Player player, String current, String target);

    /**
     * Move class represents players moves in a game.
     * 
     * @author Bilal Ekrem Harmansa
     */
    public class Move {
        int doerID; // the player who does the move
        String previous; // constructed segment-level
        String current; // constructed segment-level

        @Override
        public String toString() {
            Player player = players.get(doerID);
            return player.name + "moved a piece: " + 
                    previous + "-->" + current;
        }
    }

    /**
     * Pieces are movable objects deployed on a game. For instance, a pawn
     * in chess. 
     * The pieces that belong to each player are distinguished by color.
     * For this purpose created a enum Type, i.e. light piece, dark piece.
     * 
     * @author Bilal Ekrem Harmansa
     */
    public static class Piece {
        String location; // piece'location on the graph as pair of segment-level
        Type type;
        
        public static enum Type {
            LIGHT, DARK
        }
    }

}