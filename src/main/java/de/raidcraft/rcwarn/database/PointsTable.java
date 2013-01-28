package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;

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
                            "`accepted` TINYINT( 1 ) NOT NULL DEFAULT '0',\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addPoints(String player, String punisher, Reason reason) {
        try {
            getConnection().prepareStatement(
                    "INSERT INTO " + getTableName() + " (player, punisher, amount, reason, detail, date) " +
                            "VALUES (" +
                            "'" + player + "'" + "," +
                            "'" + punisher + "'" + "," +
                            "'" + reason.getPoints() + "'" + "," +
                            "'" + reason.getName() + "'" + "," +
                            "'" + reason.getDetail() + "'" + "," +
                            "'" + DateUtil.getCurrentDateString() + "'" +
                            ");"
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public int getAllPoints(String player) {
        int points = 0;
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player='" + player + "'").executeQuery();

            while (resultSet.next()) {
                points += resultSet.getInt("amount");
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return points;
    }

    public void setAccepted(String player) {
        try {
            getConnection().prepareStatement(
                    "UPDATE " + getTableName() + " SET accepted = '1' WHERE player = '" + player + "'").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public List<Warning> getOpenWarnings() {
        List<Warning> warnings = new ArrayList<>();
        Warning warning;
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE accepted='0'").executeQuery();

            while (resultSet.next()) {
                warning = new Warning(
                        resultSet.getString("player"),
                        resultSet.getString("punisher"),
                        Reason.getReason(resultSet.getString("reason")).clone().setDetail(resultSet.getString("detail")));
                warnings.add(warning);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return warnings;
    }
}
