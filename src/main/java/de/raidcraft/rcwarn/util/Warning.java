package de.raidcraft.rcwarn.util;

import org.bukkit.Location;

/**
 * @author Philip
 */
public class Warning {

    String player;
    String punisher;
    Reason reason;
    String date;
    Location location;

    public Warning(String player, String punisher, Reason reason, String date, Location location) {

        this.player = player;
        this.punisher = punisher;
        this.reason = reason;
        this.date = date;
        this.location = location;
    }

    public String getPlayer() {

        return player;
    }

    public String getPunisher() {

        return punisher;
    }

    public Reason getReason() {

        return reason;
    }

    public String getDate() {

        return date;
    }

    public Location getLocation() {

        return location;
    }
}
