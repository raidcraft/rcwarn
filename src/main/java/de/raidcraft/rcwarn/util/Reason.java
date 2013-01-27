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
            if(reason.getName().equalsIgnoreCase(name)) {
                reason.setDetail("");
                return reason;
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

    private String name;
    private int points;
    private String detail;

    public Reason(String name, int points) {

        this.name = name;
        this.points = points;
        reasons.add(this);
    }

    public void setDetail(String detail) {

        this.detail = detail;
    }

    public String getName() {

        return name;
    }

    public int getPoints() {

        return points;
    }

    public String getDetail() {

        return detail;
    }
}
