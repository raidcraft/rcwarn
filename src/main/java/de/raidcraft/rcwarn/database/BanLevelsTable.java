package de.raidcraft.rcwarn.database;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.util.BanLevel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class BanLevelsTable extends Table {

    public BanLevelsTable() {

        super("banlevel", "rcwarn_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`points` INT( 11 ) NOT NULL ,\n" +
                            "`duration` BIGINT( 32 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setBanLevels() {

        List<BanLevel> banLevels = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName()).executeQuery();

            while (resultSet.next()) {
                banLevels.add(new BanLevel(resultSet.getInt("points"), resultSet.getLong("duration")));
            }
            BanManager.INST.setBanLevels(banLevels);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
}
