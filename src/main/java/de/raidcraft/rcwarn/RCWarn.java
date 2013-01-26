package de.raidcraft.rcwarn;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.commands.Commands;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
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

        registerCommands(Commands.class);
        CommandBook.registerEvents(new PlayerListener());
        new Database(RaidCraft.getComponent(RaidCraftPlugin.class)).registerTable(BansTable.class, new BansTable());
        new Database(RaidCraft.getComponent(RaidCraftPlugin.class)).registerTable(PointsTable.class, new PointsTable());
    }

    @Override
    public void disable() {

    }

    public static class LocalConfiguration extends ConfigurationBase<RCWarn> {



        public LocalConfiguration(RCWarn plugin) {

            super(plugin, "config.yml");
        }
    }
}
