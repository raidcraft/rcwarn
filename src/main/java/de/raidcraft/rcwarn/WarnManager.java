package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Reason;

/**
 * @author Philip
 */
public class WarnManager {

    public static final WarnManager INST = new WarnManager();

    public void addWarning(String player, String punisher, Reason reason) {

        Database.getTable(PointsTable.class).addPoints(player, punisher, reason);
        BanManager.INST.checkPlayer(player);
    }


}
