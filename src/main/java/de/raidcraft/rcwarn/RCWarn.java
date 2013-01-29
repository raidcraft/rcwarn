package de.raidcraft.rcwarn;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.commands.AdminCommands;
import de.raidcraft.rcwarn.commands.UnbanCommand;
import de.raidcraft.rcwarn.commands.WarnCommands;
import de.raidcraft.rcwarn.database.BanLevelsTable;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.database.ReasonsTable;
import de.raidcraft.rcwarn.listener.PlayerListener;

/**
 * @author Philip
 */
@ComponentInformation(
        friendlyName = "RCWarn",
        desc = "Provides warn and ban system."
)
@Depend(plugins = {"RaidCraft-API"})
public class RCWarn extends BasePlugin implements Component {

    public LocalConfiguration config;
    public static RCWarn INST;

    @Override
    public void enable() {
        INST = this;
        this.config = configure(new LocalConfiguration(this));

        registerCommands(WarnCommands.class);
        registerCommands(AdminCommands.class);
        registerCommands(UnbanCommand.class);

        CommandBook.registerEvents(new PlayerListener());
        new Database(RaidCraft.getComponent(RaidCraftPlugin.class)).registerTable(BansTable.class, new BansTable());
        new Database(RaidCraft.getComponent(RaidCraftPlugin.class)).registerTable(PointsTable.class, new PointsTable());
        new Database(RaidCraft.getComponent(RaidCraftPlugin.class)).registerTable(ReasonsTable.class, new ReasonsTable())        ;
        new Database(RaidCraft.getComponent(RaidCraftPlugin.class)).registerTable(BanLevelsTable.class, new BanLevelsTable())        ;


        load();
    }

    @Override
    public void disable() {

    }

    public void load() {
        Database.getTable(ReasonsTable.class).addAllReasons();
        Database.getTable(BanLevelsTable.class).setBanLevels();
        WarnManager.INST.setOpenWarnings(Database.getTable(PointsTable.class).getOpenWarnings());
    }

    public static class LocalConfiguration extends ConfigurationBase<RCWarn> {

        @Setting("ban-text") public String banText = "Du wurdest %e gebannt! Schaue im Forum nach weshalb! (forum.raid-craft.de)";
        @Setting("warning-cooldown") public int warningCooldown = 180;

        public LocalConfiguration(RCWarn plugin) {

            super(plugin, "config.yml");
        }
    }
}
