package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.Ban;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class BansTable extends Table {

    public BansTable() {

        super("bans", "rcwarn_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`player` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`points` INT( 11 ) NOT NULL ,\n" +
                            "`date` VARCHAR( 64 ) NOT NULL ,\n" +
                            "`expiration` VARCHAR( 64 ) DEFAULT NULL ,\n" +
                            "`unbanned` TINYINT( 1 ) DEFAULT '0' , \n" +
                            "`posted` TINYINT( 1 ) NOT NULL DEFAULT '0',\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addBan(Ban ban) {
        try {
            getConnection().prepareStatement(
                    "INSERT INTO " + getTableName() + " (player, points, date, expiration) " +
                            "VALUES (" +
                            "'" + ban.getPlayer() + "'" + "," +
                            "'" + ban.getPoints() + "'" + "," +
                            "'" + ban.getDate() + "'" + "," +
                            "'" + ban.getExpiration() + "'" +
                            ");"
            ).execute();
            RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().setLocalBukkitBan(ban.getPlayer(), true);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public Ban getBan(String player) {
        return getLastBan(player);
    }

    public List<Ban> getBans(String player) {
        List<Ban> bans = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player='" + player + "' ORDER BY id DESC").executeQuery();

            while (resultSet.next()) {
                Ban ban = new Ban(resultSet.getString("player"), resultSet.getInt("points"), resultSet.getString("date"), resultSet.getString("expiration"), resultSet.getBoolean("unbanned"));
                bans.add(ban);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return bans;
    }

    public Ban getLastBan(String player) {
        String unbannedFilter = "";
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player='" + player + "' " + unbannedFilter + " ORDER BY id DESC").executeQuery();

            while (resultSet.next()) {
                return new Ban(resultSet.getString("player"), resultSet.getInt("points"), resultSet.getString("date"), resultSet.getString("expiration"), resultSet.getBoolean("unbanned"));
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return null;
    }

    public void unban(String player) {
        try {
            getConnection().prepareStatement(
                    "UPDATE " + getTableName() + " SET unbanned = '1' WHERE player = '" + player + "' ORDER BY ID DESC LIMIT 1").execute();
            RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().setLocalBukkitBan(player, false);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
}
