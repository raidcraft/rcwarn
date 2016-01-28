package de.raidcraft.rcwarn.database;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import de.raidcraft.util.UUIDUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Philip on 26.01.2016.
 */
@Data
@Entity
@Table(name = "rcwarn_points")
public class TPoints {

    @Id
    private int id;
    private String player;
    private String punisher;
    private int amount;
    private String reason;
    private String detail;
    private String date;
    private String world;
    private int x;
    private int y;
    private int z;
    private boolean accepted;
    private boolean expired;
    private boolean permanent;
    private boolean posted;
    private UUID playerId;

    public static void addPoints(Warning warning) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        String worldName = "";
        int x = 0;
        int y = 0;
        int z = 0;

        if (warning.getLocation() != null) {
            worldName = warning.getLocation().getWorld().getName();
            x = warning.getLocation().getBlockX();
            y = warning.getLocation().getBlockY();
            z = warning.getLocation().getBlockZ();
        }

        TPoints tPoints = new TPoints();
        tPoints.setPlayer(UUIDUtil.getNameFromUUID(warning.getPlayerId()));
        tPoints.setPlayerId(warning.getPlayerId());
        tPoints.setPunisher(warning.getPunisher());
        tPoints.setAmount(warning.getReason().getPoints());
        tPoints.setReason(warning.getReason().getName());
        tPoints.setDetail(warning.getReason().getDetail());
        tPoints.setDate(warning.getDate());
        tPoints.setWorld(worldName);
        tPoints.setX(x);
        tPoints.setY(y);
        tPoints.setZ(z);

        plugin.getDatabase().save(tPoints);
    }

    public static int getAllPoints(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        int points = 0;
        checkPointsExpiration(player);

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where()
                .eq("player_id", player)
                .or(
                        plugin.getDatabase().getExpressionFactory().and(
                                plugin.getDatabase().getExpressionFactory().eq("expired", false),
                                plugin.getDatabase().getExpressionFactory().eq("permanent", false)),
                        plugin.getDatabase().getExpressionFactory().eq("permanent", true)
                ).findList();
        if(tPointsList == null) return points;

        for(TPoints tPoints : tPointsList) {
            points += tPoints.getAmount();
        }

        return points;
    }

    public static List<Warning> getAllWarnings(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        List<Warning> warnings = new ArrayList<>();
        Warning warning;

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where().eq("player_id", player).findList();
        if(tPointsList == null) {
            return warnings;
        }

        for(TPoints tPoints : tPointsList) {
            warning = getWarningByResultSet(tPoints);
            warnings.add(warning);
        }

        return warnings;
    }

    public static List<Warning> getOpenWarnings() {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        List<Warning> warnings = new ArrayList<>();
        Warning warning;

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where().eq("accepted", false).findList();
        if(tPointsList == null) {
            return warnings;
        }

        for(TPoints tPoints : tPointsList) {
            warning = getWarningByResultSet(tPoints);
            warnings.add(warning);
        }

        return warnings;
    }

    public static Warning getLastWarning(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        Warning warning;

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where().eq("player_id", player).orderBy("id DESC").findList();
        if(tPointsList == null) {
            return null;
        }

        for(TPoints tPoints : tPointsList) {
            warning = getWarningByResultSet(tPoints);
            return warning;
        }

        return null;
    }

    public static void checkPointsExpiration(UUID player) {
        List<Warning> warnings = getAllWarnings(player);
        for (Warning warning : warnings) {
            // warning expired
            if (warning.getReason().getDuration() <= 0) {
                continue;
            }

            if (DateUtil.getTimeStamp(warning.getDate()) + warning.getReason().getDuration() * 60 * 1000 < System.currentTimeMillis()) {
                setExpiredFlag(warning);
            }
        }
    }

    public static void setAcceptedFlag(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where().eq("player_id", player).findList();
        if(tPointsList == null) return;

        for(TPoints tPoints : tPointsList) {
            tPoints.setAccepted(true);
            plugin.getDatabase().update(tPoints);
        }
    }

    public static void setExpiredFlag(Warning warning) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where()
                .eq("player_id", warning.getPlayerId()).eq("date", warning.getDate()).findList();
        if(tPointsList == null) return;

        for(TPoints tPoints : tPointsList) {
            tPoints.setExpired(true);
            plugin.getDatabase().update(tPoints);
        }
    }

    public static void setPermanentFlag(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        List<TPoints> tPointsList = plugin.getDatabase().find(TPoints.class).where()
                .eq("player_id", player).findList();
        if(tPointsList == null) return;

        for(TPoints tPoints : tPointsList) {
            tPoints.setPermanent(true);
            plugin.getDatabase().update(tPoints);
        }
    }

    private static Warning getWarningByResultSet(TPoints tPoints) {
        Reason reason = new Reason(tPoints.getReason(), tPoints.getAmount(), 0);
        reason.setDetail(tPoints.getDetail());
        Location location = null;
        if (Bukkit.getWorld(tPoints.getWorld()) != null) {
            location = new Location(Bukkit.getWorld(tPoints.getWorld()), tPoints.getX(), tPoints.getY(), tPoints.getZ());
        }
        Warning warning = new Warning(
                tPoints.getPlayerId(),
                tPoints.getPunisher(),
                reason,
                tPoints.getDate(),
                location);
        warning.setExpired(tPoints.isExpired());
        return warning;
    }
}
