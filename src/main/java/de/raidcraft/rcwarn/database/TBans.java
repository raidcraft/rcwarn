package de.raidcraft.rcwarn.database;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.Ban;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Philip on 27.01.2016.
 */
@Data
@Entity
@Table(name = "rcwarn_bans")
public class TBans {

    @Id
    private int id;
    private String player;
    private UUID playerId;
    private int points;
    private String date;
    private String expiration;
    private boolean unbanned;
    private boolean posted;

    public static void addBan(Ban ban) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        TBans tBans = new TBans();
        tBans.setPlayer(ban.getPlayerName());
        tBans.setPlayerId(ban.getPlayerId());
        tBans.setPoints(ban.getPoints());
        tBans.setDate(ban.getDate());
        tBans.setExpiration(ban.getExpiration());
        plugin.getDatabase().save(tBans);
    }

    public static Ban getBan(UUID player) {
        return getLastBan(player);
    }

    public static List<Ban> getBans(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        List<Ban> bans = new ArrayList<>();

        List<TBans> tBansList = plugin.getDatabase().find(TBans.class).where()
                .eq("player_id", player).orderBy("id DESC").findList();
        if(tBansList == null) {
            return bans;
        }

        for(TBans tBans : tBansList) {
            Ban ban = new Ban(tBans.getPlayer(),
                    tBans.getPlayerId(),
                    tBans.getPoints(),
                    tBans.getDate(),
                    tBans.getExpiration(),
                    tBans.isUnbanned());
            bans.add(ban);
        }
        return bans;
    }

    public static Ban getLastBan(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        List<TBans> tBansList = plugin.getDatabase().find(TBans.class).where()
                .eq("player_id", player).orderBy("id DESC").findList();
        if(tBansList == null) {
            return null;
        }

        for(TBans tBans : tBansList) {
            Ban ban = new Ban(tBans.getPlayer(),
                    tBans.getPlayerId(),
                    tBans.getPoints(),
                    tBans.getDate(),
                    tBans.getExpiration(),
                    tBans.isUnbanned());
            return ban;
        }
        return null;
    }

    public static void unban(UUID player) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        List<TBans> tBansList = plugin.getDatabase().find(TBans.class).where()
                .eq("player_id", player).orderBy("id DESC").findList();
        if(tBansList == null) return;

        for(TBans tBans : tBansList) {
            tBans.setUnbanned(true);
            plugin.getDatabase().update(tBans);
            return;
        }
    }
}
