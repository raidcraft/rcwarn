package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.rcwarn.WarnManager;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class PointsTable extends Table {

    public PointsTable() {

        super("points", "rcwarn_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`player` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`punisher` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`amount` INT( 11 ) NOT NULL ,\n" +
                            "`reason` VARCHAR( 64 ) NOT NULL ,\n" +
                            "`detail` VARCHAR( 200 ) NOT NULL ,\n" +
                            "`date` VARCHAR( 64 ) NOT NULL ,\n" +
                            "`world` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`x` INT( 11 ) NOT NULL ,\n" +
                            "`y` INT( 11 ) NOT NULL ,\n" +
                            "`z` INT( 11 ) NOT NULL ,\n" +
                            "`accepted` TINYINT( 1 ) NOT NULL DEFAULT '0',\n" +
                            "`expired` TINYINT( 1 ) NOT NULL DEFAULT '0',\n" +
                            "`permanent` TINYINT( 1 ) NOT NULL DEFAULT '0',\n" +
                            "`posted` TINYINT( 1 ) NOT NULL DEFAULT '0',\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addPoints(Warning warning) {
        try {
            String worldName = "";
            int x = 0;
            int y = 0;
            int z = 0;

            if(warning.getLocation() != null) {
                worldName = warning.getLocation().getWorld().getName();
                x = warning.getLocation().getBlockX();
                y = warning.getLocation().getBlockY();
                z = warning.getLocation().getBlockZ();
            }

            getConnection().prepareStatement(
                    "INSERT INTO " + getTableName() + " (player, punisher, amount, reason, detail, date, world, x, y, z) " +
                            "VALUES (" +
                            "'" + warning.getPlayer() + "'" + "," +
                            "'" + warning.getPunisher() + "'" + "," +
                            "'" + warning.getReason().getPoints() + "'" + "," +
                            "'" + warning.getReason().getName() + "'" + "," +
                            "'" + warning.getReason().getDetail() + "'" + "," +
                            "'" + warning.getDate() + "'" + "," +
                            "'" + worldName + "'" + "," +
                            "'" + x + "'" + "," +
                            "'" + y + "'" + "," +
                            "'" + z + "'" +
                            ");"
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public int getAllPoints(String player) {
        int points = 0;
        checkPointsExpiration(player);
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player='" + player + "' AND (expired='0' OR permanent='1')").executeQuery();

            while (resultSet.next()) {
                points += resultSet.getInt("amount");
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }

        return points;
    }

    public List<Warning> getAllWarnings(String player) {
        List<Warning> warnings = new ArrayList<>();
        Warning warning;
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "'").executeQuery();

            while (resultSet.next()) {
                warning = getWarningByResultSet(resultSet);
                warnings.add(warning);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return warnings;
    }

    public List<Warning> getOpenWarnings() {
        List<Warning> warnings = new ArrayList<>();
        Warning warning;
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE accepted='0'").executeQuery();

            while (resultSet.next()) {
                warning = getWarningByResultSet(resultSet);
                warnings.add(warning);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return warnings;
    }

    public Warning getLastWarning(String player) {
        Warning warning;
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' ORDER BY id DESC").executeQuery();

            while (resultSet.next()) {
                return getWarningByResultSet(resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return null;
    }

    public void checkPointsExpiration(String player) {
        List<Warning> warnings = getAllWarnings(player);
        for(Warning warning : warnings) {
            // warning expired
            if(warning.getReason().getDuration() <= 0) {
                continue;
            }

            if(DateUtil.getTimeStamp(warning.getDate()) + warning.getReason().getDuration()*60*1000 < System.currentTimeMillis()) {
                setExpired(warning);
            }
        }
    }

    public void setAccepted(String player) {
        try {
            getConnection().prepareStatement(
                    "UPDATE " + getTableName() + " SET accepted = '1' WHERE player = '" + player + "'").execute();
            WarnManager.INST.setOpenWarnings(getOpenWarnings());
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public void setExpired(Warning warning) {
        try {
            getConnection().prepareStatement(
                    "UPDATE " + getTableName() + " SET expired = '1' WHERE player = '" + warning.getPlayer() + "' AND date = '" +  warning.getDate() + "'").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public void setPermanent(String player) {
        try {
            getConnection().prepareStatement(
                    "UPDATE " + getTableName() + " SET permanent='1' WHERE player = '" + player + "'").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    private Warning getWarningByResultSet(ResultSet resultSet) throws SQLException {
        Reason reason = new Reason(resultSet.getString("reason"), resultSet.getInt("amount"), 0);
        reason.setDetail(resultSet.getString("detail"));
        Location location = null;
        if(Bukkit.getWorld(resultSet.getString("world")) != null) {
            location = new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
        }
        Warning warning = new Warning(
                resultSet.getString("player"),
                resultSet.getString("punisher"),
                reason,
                resultSet.getString("date"),
                location);
        warning.setExpired(resultSet.getBoolean("expired"));
        return warning;
    }
}
