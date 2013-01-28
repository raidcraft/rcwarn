package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.BanLevel;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

/**
 * @author Philip
 */
public class BanManager {

    public static final BanManager INST = new BanManager();

    private List<BanLevel> banLevels;

    public void setBanLevels(List<BanLevel> newBanLevels) {
        banLevels = newBanLevels;
    }

    public BanLevel getNextBanLevel(int points) {
        BanLevel nextBanLevel = null;
        for(BanLevel banLevel : banLevels) {
            if(banLevel.getPoints() < points) {
                continue;
            }
            if(nextBanLevel == null || nextBanLevel.getPoints() > banLevel.getPoints()) {
                nextBanLevel = banLevel;
            }
        }
        return nextBanLevel;
    }

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
        String expiration = nextBan.getExpirationFromNow();
        Ban newBan = new Ban(player, playerPoints, DateUtil.getCurrentDateString(), expiration);
        Database.getTable(PointsTable.class).setAccepted(player);
        Database.getTable(BansTable.class).addBan(newBan);
        kickBannedPlayer(player, newBan);

        Bukkit.broadcastMessage(ChatColor.DARK_RED + player + " wurde gebannt (" + newBan.getEmbellishedExpiration() + ")");
    }

    public void kickBannedPlayer(String player, Ban ban) {
        if(Bukkit.getPlayer(player) != null) {
            String expiration = ban.getExpiration();
            String info = RCWarn.INST.config.banText.replace("%e", ban.getEmbellishedExpiration());
            Bukkit.getPlayer(player).kickPlayer(info);
        }
    }

    public void kickBannedPlayer(AsyncPlayerPreLoginEvent event, Ban ban) {
        String expiration = ban.getExpiration();
        String info = RCWarn.INST.config.banText.replace("%e", ban.getEmbellishedExpiration());
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, info);
    }
}
