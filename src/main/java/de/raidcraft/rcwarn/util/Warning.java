package de.raidcraft.rcwarn.util;

import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @author Philip
 */
public class Warning {

    @Getter
    UUID playerId;
    String punisher;
    Reason reason;
    String date;
    Location location;
    private boolean expired = false;

    public Warning(UUID playerId, String punisher, Reason reason, String date, Location location) {

        this.playerId = playerId;
        this.punisher = punisher;
        this.reason = reason;
        this.date = date;
        this.location = location;
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

    public boolean isExpired() {

        return expired;
    }

    public void setExpired(boolean expired) {

        this.expired = expired;
    }
}
