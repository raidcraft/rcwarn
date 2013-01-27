package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.rcwarn.util.Reason;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Philip
 */
public class ReasonsTable extends Table {

    public ReasonsTable() {

        super("reasons", "rcwarn_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`name` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`points` VARCHAR( 64 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addAllReasons() {
        Reason.cleanReasons();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName()).executeQuery();

            while (resultSet.next()) {
                new Reason(resultSet.getString("name"), resultSet.getInt("points"));
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

}
