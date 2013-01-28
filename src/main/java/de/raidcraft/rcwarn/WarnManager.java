package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip
 */
public class WarnManager {

    public static final WarnManager INST = new WarnManager();

    private Map<String, Warning> openWarnings = new HashMap<>();

    public void addWarning(String player, String punisher, Reason reason) {

        Database.getTable(PointsTable.class).addPoints(player, punisher, reason);
        openWarnings.put(player, new Warning(player, punisher, reason));
        BanManager.INST.checkPlayer(player);
    }

    public void setOpenWarnings(List<Warning> warnings) {
        openWarnings.clear();
        for(Warning warning : warnings) {
            openWarnings.put(warning.getPlayer(), warning);
        }
    }

    public Warning getOpenWarning(String player) {
        return openWarnings.get(player);
    }
}
