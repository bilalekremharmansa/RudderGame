package com.bilalekrem.ruddergame.game;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonIgnore
    public List<Game.Piece> pieces; // Each players pieces holds in here.

    private final int hash;

    public Player(int ID) {
        this.ID = ID;
        this.name = "Unknown";
        hash = 31*name.hashCode() + 37*ID;
    }

    public Player(int ID, String username) {
        this(ID);
        this.name = username;
    }

    /** JSON Deserializing */
    @JsonCreator
    private Player(@JsonProperty("id") int ID,
                    @JsonProperty("name") String username,
                    @JsonProperty("pieceType") Game.PieceType type) {
        this(ID);
        this.name = username;
        this.pieceType = type;
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

    @Override
    public String toString() {
        return name + " - " + ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
       
        if ( !(o instanceof Player) ) return false;

        Player p = (Player)o;
        
        if (this.name.equals(p.name) && this.ID == p.ID 
                                    && this.pieceType == p.pieceType) return true;

        return false; 
    }

    @Override
    public int hashCode() {
        return hash;
    }
}