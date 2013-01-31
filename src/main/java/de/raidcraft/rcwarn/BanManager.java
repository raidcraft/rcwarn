package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.BanLevel;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

    public BanLevel getHighestBanLevel() {
        BanLevel nextBanLevel = null;
        for(BanLevel banLevel : banLevels) {
            if(nextBanLevel == null || nextBanLevel.getPoints() < banLevel.getPoints()) {
                nextBanLevel = banLevel;
            }
        }
        return nextBanLevel;
    }

    public void checkPlayer(String player) {

        BanLevel preBanLevel = null;
        int playerPoints = Database.getTable(PointsTable.class).getAllPoints(player);
        for(BanLevel banLevel : banLevels) {
            if(banLevel.getPoints() > playerPoints) {
                continue;
            }
            preBanLevel = banLevel;
        }

        // no ban level reached
        if(preBanLevel == null) {
            return;
        }

        /*
         * get sure that ban level not reached before
         */
        Ban lastBan = Database.getTable(BansTable.class).getLastBan(player);                        // get last ban
        List<Warning> allWarnings = Database.getTable(PointsTable.class).getAllWarnings(player);    // get all warnings
        // sort warnings
        SortedMap<Long, Warning> orderedWarnings = new TreeMap<>();
        for(Warning warning : allWarnings) {
            if(warning.isExpired()) continue;
            orderedWarnings.put(DateUtil.getTimeStamp(warning.getDate()), warning);
        }

        // check if points was below preBanLevel after last ban
        int totalPoints = 0;
        boolean wasBelow = false;
        for(Map.Entry<Long, Warning> entry : orderedWarnings.entrySet()) {
            Long date = entry.getKey();
            Warning warning = entry.getValue();
            totalPoints += warning.getReason().getPoints();
            if((lastBan == null || date > DateUtil.getTimeStamp(lastBan.getDate())) && totalPoints < preBanLevel.getPoints()) {
                wasBelow = true;
                break;
            }
        }
        if(!wasBelow) {
            return;
        }

        // ban player
        String expiration = preBanLevel.getExpirationFromNow();
        Ban newBan = new Ban(player, playerPoints, DateUtil.getCurrentDateString(), expiration);
        Database.getTable(PointsTable.class).setAccepted(player);
        Database.getTable(BansTable.class).addBan(newBan);
        kickBannedPlayer(player, newBan);

        Bukkit.broadcastMessage(ChatColor.DARK_RED + player + " wurde gebannt (" + newBan.getEmbellishedExpiration() + ")!");
    }

    public void kickBannedPlayer(String player, Ban ban) {
        if(Bukkit.getPlayer(player) != null) {
            String info = RCWarn.INST.config.banText.replace("%e", ban.getEmbellishedExpiration());
            Bukkit.getPlayer(player).kickPlayer(info);
        }
    }

    public void kickBannedPlayer(AsyncPlayerPreLoginEvent event, Ban ban) {
        String info = RCWarn.INST.config.banText.replace("%e", ban.getEmbellishedExpiration());
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, info);
    }

    public void setLocalBukkitBan(String player, boolean banned) {
        OfflinePlayer offlinePlayer = RCWarn.INST.getServer().getOfflinePlayer(player);
        if(offlinePlayer != null) {
            offlinePlayer.setBanned(banned);
        }
    }
}
