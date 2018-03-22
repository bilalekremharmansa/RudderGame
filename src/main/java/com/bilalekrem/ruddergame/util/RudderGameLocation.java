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