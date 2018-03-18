package com.bilalekrem.ruddergame.game;

import java.util.List;

/**
 * Player class models person, who plays the game. 
 * 
 * @author Bilal Ekrem Harmansa
 */
public class Player {
    // player ids will be used to distinguish Player in Game class. 
    int ID; 
    String name; // players name
    Game.Piece.Type pieceType; //  what is the color(type) of Player ?
    List<Game.Piece> pieces; // Each players pieces holds in here.
}