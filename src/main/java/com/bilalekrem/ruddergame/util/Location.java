package com.bilalekrem.ruddergame.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;



/**
 * Location class will be a bridge between Node class and Piece class
 * Just wanted to separate Node and Piece. 
 * 
 * Also, this class is an immutable class. 
 * 
 * @author Bilal Ekrem Harmansa
 */
/** baeldung.com/jackson-inheritance */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "type")
@JsonSubTypes({ 
  @Type(value = RudderGameLocation.class, name = "ruddergame"), 
})
public class Location {
    public final Segment segment;
    public final int level;

    protected int hash;
    
    public Location(@JsonProperty("segment") Segment segment, 
                    @JsonProperty("level") int level) {
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