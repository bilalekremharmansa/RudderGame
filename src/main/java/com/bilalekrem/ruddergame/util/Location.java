package com.bilalekrem.ruddergame.util;

/**
 * Location class will be a bridge between Node class and Piece class
 * Just wanted to separate Node and Piece. 
 * 
 * Also, this class is an immutable class. 
 * 
 * @author Bilal Ekrem Harmansa
 */
public class Location {
    public final Segment segment;
    public final int level;

    protected int hash;
    
    public Location(Segment segment, int level) {
        this.segment = segment;
        this.level = level;

        hash=31*segment.ordinal() + 37*level;
    }

    public static String parseString(Segment segment, int level) {
        return segment + "-" + level;
    }

    @Override
    public String toString(){
        return segment.toString() + '-' + level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
       
        if ( !(o instanceof Location) ) return false;

        Location n = (Location)o;
        
        if (this.segment == n.segment && this.level == n.level) return true;

        return false; 
    }

    @Override
    public int hashCode() {
        return hash;
    }

}