package de.raidcraft.rcwarn.util;

/**
 * @author Philip
 */
public class Warning {

    String player;
    String punisher;
    Reason reason;

    public Warning(String player, String punisher, Reason reason) {

        this.player = player;
        this.punisher = punisher;
        this.reason = reason;
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
}
