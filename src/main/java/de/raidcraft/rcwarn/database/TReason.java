package de.raidcraft.rcwarn.database;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.Reason;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Philip on 26.01.2016.
 */
@Data
@Entity
@Table(name = "rcwarn_reasons")
public class TReason {

    @Id
    private int id;
    private String name;
    private int points;
    private String aliases;
    private long duration;
    private String description;


    public static void addAllReasons() {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);

        Reason.cleanReasons();

        List<TReason> tReasons = plugin.getDatabase().find(TReason.class).findList();
        if(tReasons != null)
            for(TReason tReason : tReasons) {
                Reason reason = new Reason(tReason.getName(), tReason.getPoints(), tReason.getDuration());
                String[] aliases = tReason.getAliases().replace(" ", "").split(",");
                if (aliases != null) {
                    for (String alias : aliases) {
                        reason.addAlias(alias);
                    }
                }
                Reason.addReason(reason);
            }
    }
}
