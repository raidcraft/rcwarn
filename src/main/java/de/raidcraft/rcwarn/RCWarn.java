package de.raidcraft.rcwarn;

import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.commands.*;
import de.raidcraft.rcwarn.database.BanLevelsTable;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.database.ReasonsTable;
import de.raidcraft.rcwarn.listener.PlayerListener;

import java.net.URL;
import java.net.URLConnection;

/**
 * @author Philip
 */
@ComponentInformation(
        friendlyName = "RCWarn",
        desc = "Provides warn and ban system."
)
@Depend(plugins = {"RaidCraft-API"})
public class RCWarn extends BasePlugin {

    public LocalConfiguration config;
    public static RCWarn INST;

    @Override
    public void enable() {
        INST = this;
        this.config = configure(new LocalConfiguration(this));

        registerCommands(WarnCommand.class);
        registerCommands(AdminCommands.class);
        registerCommands(UnbanCommand.class);
        registerCommands(WarningsInfoCommand.class);
        registerCommands(BansInfoCommand.class);

        registerEvents(new PlayerListener());
        registerTable(BansTable.class, new BansTable());
        registerTable(PointsTable.class, new PointsTable());
        registerTable(ReasonsTable.class, new ReasonsTable());
        registerTable(BanLevelsTable.class, new BanLevelsTable());

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

        @Setting("ban-text") public String banText = "Du wurdest %e gebannt! Informiere Dich im Forum! (forum.raid-craft.de)";
        @Setting("warning-cooldown") public int warningCooldown = 180;
        @Setting("supporter-max-warn-points") public int supporterMaxWarnPoints = 2;
        @Setting("postbot-url") public String postbotURL = "http://apps.raid-craft.de/woltlab_postbot/scripts/rcwarn/rcwarn.php";

        public LocalConfiguration(RCWarn plugin) {

            super(plugin, "config.yml");
        }
    }

    public void postThreads() {
        try {
            URL myURL = new URL(config.postbotURL);
            URLConnection myURLConnection = myURL.openConnection();
            myURLConnection.connect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
