package com.bilalekrem.ruddergame.util;

public class RudderGameLocation extends Location{

    /**
     * There is a special case that distinguishes RudderGameLocation
     * from ruddergame.util.Location. If Segment is Center than
     * do not include level while calculating hashCode.
     * 
     */
    public RudderGameLocation(Segment segment, int level) {
        super(segment, level);

        hash=31*segment.ordinal();
    }

    /**
     * There is a hierachy between Segments. For example, in the game, 
     * a move can be A1-B1. While finding the distance between two Location
     * we can use String comparison and also there is special case H to A or 
     * A to H.
     * 
     * ps. there is no checking level control.
     * 
     * @param location to be calculated distance that from caller class to location.
     * 
     * @return distance between segments. There is a special case between H to A.
     */
    public int segmentDistance(RudderGameLocation location) {
        if(this.segment == Segment.A && location.segment == Segment.H) return 1;
        else if(this.segment == Segment.H && location.segment == Segment.A) return 1;
    
        /** String comparison to determine */
        return Math.abs(this.segment.compareTo(location.segment));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
       
        if ( !(o instanceof RudderGameLocation) ) return false;

        Location n = (RudderGameLocation)o;

        /**
        * if segment is CENTER, no matter that the value of level.
        * Otherwise need to sure their, both, levels are same.
         */
        if (this.segment == n.segment && this.segment == Segment.CENTER) return true;
        if (this.segment == n.segment && this.level == n.level) return true;

        return false; 
    }

}