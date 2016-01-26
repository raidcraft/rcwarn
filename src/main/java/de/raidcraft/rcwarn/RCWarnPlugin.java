package de.raidcraft.rcwarn;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcmultiworld.BungeeManager;
import de.raidcraft.rcmultiworld.RCMultiWorldPlugin;
import de.raidcraft.rcwarn.commands.AdminCommands;
import de.raidcraft.rcwarn.commands.BansInfoCommand;
import de.raidcraft.rcwarn.commands.UnbanCommand;
import de.raidcraft.rcwarn.commands.WarnCommand;
import de.raidcraft.rcwarn.commands.WarningsInfoCommand;
import de.raidcraft.rcwarn.database.*;
import de.raidcraft.rcwarn.listener.PlayerListener;
import de.raidcraft.rcwarn.multiworld.PlayerGetWarningMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
//@ComponentInformation(
//        friendlyName = "RCWarn",
//        desc = "Provides warn and ban system."
//)
public class RCWarnPlugin extends BasePlugin {

    private LocalConfiguration config;
    private BungeeManager bungeeManager;
    private BanManager banManager;
    private WarnManager warnManager;

    @Override
    public void enable() {

        config = configure(new LocalConfiguration(this));

        registerCommands(WarnCommand.class);
        registerCommands(AdminCommands.class);
        registerCommands(UnbanCommand.class);
        registerCommands(WarningsInfoCommand.class);
        registerCommands(BansInfoCommand.class);

        registerEvents(new PlayerListener());
        registerTable(BansTable.class, new BansTable());
        registerTable(PointsTable.class, new PointsTable());

        bungeeManager = RaidCraft.getComponent(RCMultiWorldPlugin.class).getBungeeManager();
        banManager = new BanManager(this);
        warnManager = new WarnManager(this);

        bungeeManager.registerBungeeMessage(PlayerGetWarningMessage.class);

        reload();
    }

    @Override
    public void disable() {

    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> classes = new ArrayList<>();

        classes.add(TReason.class);
        classes.add(TBanLevels.class);
        //classes.add(TPoints.class);

        return classes;
    }

    @Override
    public void reload() {
        config.reload();
        TReason.addAllReasons();
        TBanLevels.setBanLevels();
        warnManager.setOpenWarnings(Database.getTable(PointsTable.class).getOpenWarnings());
    }

    public static class LocalConfiguration extends ConfigurationBase<RCWarnPlugin> {

        @Setting("ban-text")
        public String banText = "Du wurdest %e gebannt! Informiere Dich im Forum! (forum.raid-craft.de)";
        @Setting("warning-cooldown")
        public int warningCooldown = 180;
        @Setting("supporter-max-warn-points")
        public int supporterMaxWarnPoints = 2;
        @Setting("postbot-url")
        public String postbotURL = "http://apps.srvweb/woltlab_postbot/scripts/rcwarn/rcwarn.php";

        public LocalConfiguration(RCWarnPlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    public LocalConfiguration getConfig() {

        return config;
    }

    public BungeeManager getBungeeManager() {

        return bungeeManager;
    }

    public BanManager getBanManager() {

        return banManager;
    }

    public WarnManager getWarnManager() {

        return warnManager;
    }
}
