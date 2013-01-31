package de.raidcraft.rcwarn.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class Reason {

    private static List<Reason> reasons = new ArrayList<>();

    public static Reason getReason(String name) {
        for(Reason reason : reasons) {
            for(String alias : reason.getAliases()) {
                if(alias.equalsIgnoreCase(name)) {
                    reason.setDetail("");
                    return reason;
                }
            }
        }
        return null;
    }

    public static List<String> getAllReasonNames() {
        List<String> reasonNames = new ArrayList<>();
        for(Reason reason : reasons) {
            reasonNames.add(reason.getName());
        }
        return reasonNames;
    }

    public static void cleanReasons() {
        reasons.clear();
    }

    public static void addReason(Reason reason) {
        reasons.add(reason);
    }

    private String name;
    private List<String> aliases = new ArrayList<>();
    private int points;
    private String detail = "";
    private long duration;

    public Reason(String name, int points, long duration) {

        this.name = name;
        addAlias(name);
        this.points = points;
        this.duration = duration;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public Reason setDetail(String detail) {

        this.detail = detail;
        return this;
    }

    public String getName() {

        return name;
    }

    public List<String> getAliases() {

        return aliases;
    }

    public int getPoints() {

        return points;
    }

    public String getDetail() {

        return detail;
    }

    public long getDuration() {

        return duration;
    }

    public Reason clone() {
        Reason newReason = new Reason(name, points, duration);
        newReason.setDetail(detail);
        return newReason;
    }
}
