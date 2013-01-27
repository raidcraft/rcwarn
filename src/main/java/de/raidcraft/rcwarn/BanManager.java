package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.BanLevel;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class BanManager {

    public static final BanManager INST = new BanManager();

    private List<BanLevel> banLevels = new ArrayList<>();

    public void checkPlayer(String player) {

        BanLevel nextBan = null;
        int playerPoints = Database.getTable(PointsTable.class).getAllPoints(player);
        for(BanLevel banLevel : banLevels) {
            if(banLevel.getPoints() > playerPoints) {
                continue;
            }
            nextBan = banLevel;
        }

        // not ban level reached
        if(nextBan == null) {
            return;
        }

        // get sure that ban level not reached before
        Ban lastBan = Database.getTable(BansTable.class).getBan(player);
        if(lastBan != null && lastBan.getPoints() >= nextBan.getPoints()) {
            return;
        }

        // ban player
        Ban newBan = new Ban(player, playerPoints, DateUtil.getCurrentDateString(), nextBan.getExpirationFromNow());
        Database.getTable(BansTable.class).addBan(newBan);
        kickBannedPlayer(player);

        //TODO broadcast
    }

    public void kickBannedPlayer(String player) {
        if(Bukkit.getPlayer(player) != null) {
            //TODO show temp duration
            Bukkit.getPlayer(player).kickPlayer(RCWarn.INST.config.banText);
        }
    }

}
