package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip
 */
public class WarnManager {

    public static final WarnManager INST = new WarnManager();

    private Map<String, Warning> openWarnings = new HashMap<>();

    public Warning addWarning(String player, Player punisher, Reason reason) {

        Warning warning = new Warning(player, punisher.getName(), reason, DateUtil.getCurrentDateString(), punisher.getLocation());
        Database.getTable(PointsTable.class).addPoints(warning);
        openWarnings.put(player, warning);
        BanManager.INST.checkPlayer(player);
        return warning;
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

    public void removeOpenWarning(String player) {
        openWarnings.remove(player);
    }
}
