package de.raidcraft.rcwarn.util;

import de.raidcraft.util.DateUtil;

/**
 * @author Philip
 */
public class BanLevel {

    private int points;
    private long duration;  // duration in minutes

    public BanLevel(int points, long duration) {

        this.points = points;
        this.duration = duration;
    }

    public int getPoints() {

        return points;
    }

    public long getDuration() {

        return duration;
    }

    public String getExpirationFromNow() {
        if(getDuration() <= 0) {
            return null;
        }

        return DateUtil.getDateString(System.currentTimeMillis() + getDuration()*60*1000);
    }
}
