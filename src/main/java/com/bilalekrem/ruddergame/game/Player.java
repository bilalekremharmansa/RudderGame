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
    int ID; 
    String name; // players name
    Game.PieceType pieceType; //  what is the color(type) of Player ?
    List<Game.Piece> pieces; // Each players pieces holds in here.

    /**
     * if user plays two games in a row. 
     * Resets previous game before the new one starts.
     */
    public void reset () {
        pieceType = null;
        pieces = new ArrayList<>();
    }
}