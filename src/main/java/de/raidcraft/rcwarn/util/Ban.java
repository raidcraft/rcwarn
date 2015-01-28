package de.raidcraft.rcwarn.util;

import de.raidcraft.util.DateUtil;
import lombok.Getter;

import java.util.UUID;

/**
 * @author Philip
 */
public class Ban {

    private String player;
    @Getter
    private UUID playerId;
    private int points;
    private String date;
    private String expiration;
    private boolean unbanned = false;

    public Ban(String player, UUID playerId, int points, String date, String expiration) {

        this.player = player;
        this.playerId = playerId;
        this.points = points;
        this.date = date;
        this.expiration = expiration;
    }

    public Ban(String player, UUID playerId, int points, String date, String expiration, boolean unbanned) {

        this(player, playerId, points, date, expiration);
        this.unbanned = unbanned;
    }

    public String getPlayerName() {

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
        if (!isTemporary()) {
            return "permanent";
        } else {
            return "bis " + getExpiration();
        }
    }

    public boolean isExpired() {
        if (unbanned) return true;
        if (!isTemporary()) return false;
        if (DateUtil.getTimeStamp(getExpiration()) < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    public boolean isTemporary() {
        if (getExpiration() == null || getExpiration().equalsIgnoreCase("") || getExpiration().equalsIgnoreCase("null")) {
            return false;
        }
        return true;
    }
}
