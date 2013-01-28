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
                            "`points` INT( 11 ) NOT NULL ,\n" +
                            "`aliases` VARCHAR( 500 ) NOT NULL DEFAULT ''" + " ,\n" +
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
                Reason reason = new Reason(resultSet.getString("name"), resultSet.getInt("points"));
                String[] aliases = resultSet.getString("aliases").split(",");
                if(aliases != null) {
                    for(String alias : aliases) {
                        reason.addAlias(alias);
                    }
                }
                Reason.addReason(reason);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

}
