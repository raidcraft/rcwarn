package de.raidcraft.rcwarn.util;

import de.raidcraft.util.DateUtil;

/**
 * @author Philip
 */
public class Ban {

    private String player;
    private int points;
    private String date;
    private String expiration;

    public Ban(String player, int points, String date, String expiration) {

        this.player = player;
        this.points = points;
        this.date = date;
        this.expiration = expiration;
    }

    public String getPlayer() {

        return player;
    }

    public int getPoints() {

        return points;
    }

    public String getDate() {

        return date;
    }

    public String getExpiration() {

        return expiration;
    }

    public String getEmbellishedExpiration() {
        if(getExpiration() == null) {
            return "permanent";
        }
        else {
            return "bis " + getExpiration();
        }
    }

    public boolean isExpired() {
        if(getExpiration() != null && DateUtil.getTimeStamp(getExpiration()) < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }
}
