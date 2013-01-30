package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.util.Ban;

import java.sql.ResultSet;
import java.sql.SQLException;

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
            BanManager.INST.setLocalBukkitBan(ban.getPlayer(), true);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public Ban getBan(String player) {
        return getLastBan(player, false);
    }

    public Ban getLastBan(String player) {
        return getLastBan(player, true);
    }

    public Ban getLastBan(String player, boolean alsoUnbanned) {
        String unbannedFilter = "";
        if(!alsoUnbanned) {
            unbannedFilter = "AND unbanned='0'";
        }
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
            BanManager.INST.setLocalBukkitBan(player, false);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
}
