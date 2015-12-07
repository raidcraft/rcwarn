package de.raidcraft.rcwarn;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.BanLevel;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.*;

/**
 * @author Philip
 */
public class BanManager {

    private RCWarnPlugin plugin;
    private List<BanLevel> banLevels;

    public BanManager(RCWarnPlugin plugin) {

        this.plugin = plugin;
    }

    public void setBanLevels(List<BanLevel> newBanLevels) {
        banLevels = newBanLevels;
    }

    public BanLevel getNextBanLevel(int points) {
        BanLevel nextBanLevel = null;
        for (BanLevel banLevel : banLevels) {
            if (banLevel.getPoints() < points && nextBanLevel != null) {
                continue;
            }
            if (nextBanLevel == null || nextBanLevel.getPoints() > banLevel.getPoints()) {
                nextBanLevel = banLevel;
            }
        }
        return nextBanLevel;
    }

    public BanLevel getHighestBanLevel() {
        BanLevel nextBanLevel = null;
        for (BanLevel banLevel : banLevels) {
            if (nextBanLevel == null || nextBanLevel.getPoints() < banLevel.getPoints()) {
                nextBanLevel = banLevel;
            }
        }
        return nextBanLevel;
    }

    public void checkPlayer(UUID playerId) {

        int playerPoints = Database.getTable(PointsTable.class).getAllPoints(playerId);
        BanLevel nextBanLevel = null;
        // get highest reached ban level
        for (BanLevel banLevel : banLevels) {
            if (banLevel.getPoints() > playerPoints) {
                continue;
            }
            if (nextBanLevel == null || nextBanLevel.getPoints() < banLevel.getPoints()) {
                nextBanLevel = banLevel;
            }
        }

        // if player hasn't reached any level
        if (nextBanLevel == null) {
            return;
        }

        /*
         * get sure that ban level not reached before
         */
        Ban lastBan = Database.getTable(BansTable.class).getLastBan(playerId);                        // get last ban
        List<Warning> allWarnings = Database.getTable(PointsTable.class).getAllWarnings(playerId);    // get all warnings
        // sort all not expired warnings
        SortedMap<Long, Warning> orderedWarnings = new TreeMap<>();
        for (Warning warning : allWarnings) {
            if (warning.isExpired()) continue;
            orderedWarnings.put(DateUtil.getTimeStamp(warning.getDate()), warning);
        }

        // check if points was below preBanLevel after last ban
        int totalPoints = 0;
        boolean wasBelow = false;
        for (Map.Entry<Long, Warning> entry : orderedWarnings.entrySet()) {
            Long date = entry.getKey();
            Warning warning = entry.getValue();
            totalPoints += warning.getReason().getPoints();
            if (lastBan == null || (date > DateUtil.getTimeStamp(lastBan.getDate()) && totalPoints < nextBanLevel.getPoints())) {
                wasBelow = true;
                break;
            }
        }
        if (!wasBelow) {
            return;
        }

        // ban player
        String expiration = nextBanLevel.getExpirationFromNow();
        String playerName = UUIDUtil.getNameFromUUID(playerId);
        Ban newBan = new Ban(playerName, playerId, playerPoints, DateUtil.getCurrentDateString(), expiration);
        Database.getTable(PointsTable.class).setAccepted(playerId);
        Database.getTable(PointsTable.class).setPermanent(playerId);
        Database.getTable(BansTable.class).addBan(newBan);
        kickBannedPlayer(playerId, newBan);
        //        RCWarn.INST.postThreads();
        Bukkit.broadcastMessage(ChatColor.DARK_RED + playerName + " wurde gebannt (" + newBan.getEmbellishedExpiration() + ")!");
    }

    public void kickBannedPlayer(UUID player, Ban ban) {
        if (Bukkit.getPlayer(player) != null) {
            String info = plugin.getConfig().banText.replace("%e", ban.getEmbellishedExpiration());
            Bukkit.getPlayer(player).kickPlayer(info);
        }
    }

    public void kickBannedPlayer(AsyncPlayerPreLoginEvent event, Ban ban) {
        String info = plugin.getConfig().banText.replace("%e", ban.getEmbellishedExpiration());
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, info);
    }

    public void setLocalBukkitBan(UUID player, boolean banned) {
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(player);
        if (offlinePlayer != null) {
            offlinePlayer.setBanned(banned);
        }
    }
}
