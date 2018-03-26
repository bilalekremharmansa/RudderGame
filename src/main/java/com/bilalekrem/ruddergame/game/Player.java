package com.bilalekrem.ruddergame.game;

import java.util.List;
import java.util.ArrayList;

/**
 * Player class models person, who plays the game. 
 * 
 * @author Bilal Ekrem Harmansa
 */
public class Player {
    // player ids will be used to distinguish Player in Game class. 
    public int ID; 
    public String name; // players name
    public Game.PieceType pieceType; //  what is the color(type) of Player ?
    public List<Game.Piece> pieces; // Each players pieces holds in here.

    public Player(int ID, String username) {
        this.ID = ID;
        this.name = username;
    }

    public void setPieceType(Game.PieceType type) {
        this.pieceType = type;
    }

    public void addPiece(Game.Piece piece) {
        this.pieces.add(piece);
    }

    /**
     * if user plays two games in a row. 
     * Resets previous game before the new one starts.
     */
    public void reset () {
        pieceType = null;
        pieces = new ArrayList<>();
    }
}