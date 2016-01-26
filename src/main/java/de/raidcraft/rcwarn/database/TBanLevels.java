package de.raidcraft.rcwarn.database;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.BanLevel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philip on 26.01.2016.
 */
@Data
@Entity
@Table(name = "rcwarn_banlevel")
public class TBanLevels {

    @Id
    private int id;
    private int points;
    private long duration;

    public static void setBanLevels() {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        List<BanLevel> banLevels = new ArrayList<>();

        List<TBanLevels> tBanLevelList = plugin.getDatabase().find(TBanLevels.class).findList();
        if(tBanLevelList == null) return;
        for(TBanLevels tBanLevels : tBanLevelList) {
            banLevels.add(new BanLevel(tBanLevels.getPoints(), tBanLevels.getDuration()));
        }
        RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().setBanLevels(banLevels);
    }
}
