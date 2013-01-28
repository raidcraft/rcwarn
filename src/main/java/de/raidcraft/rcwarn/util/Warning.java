package de.raidcraft.rcwarn.util;

/**
 * @author Philip
 */
public class Warning {

    String player;
    String punisher;
    Reason reason;
    String date;

    public Warning(String player, String punisher, Reason reason, String date) {

        this.player = player;
        this.punisher = punisher;
        this.reason = reason;
        this.date = date;
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
}
