package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;

import java.sql.SQLException;

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
                            "`amount` INT( 11 ) NOT NULL ,\n" +
                            "`reason` VARCHAR( 64 ) NOT NULL ,\n" +
                            "`detail` VARCHAR( 200 ) NOT NULL ,\n" +
                            "`date` VARCHAR( 64 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

}
