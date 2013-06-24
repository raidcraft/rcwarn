package de.raidcraft.rcwarn;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.multiworld.PlayerGetWarningMessage;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip
 */
public class WarnManager {

    private RCWarnPlugin plugin;
    private Map<String, Warning> openWarnings = new HashMap<>();

    public WarnManager(RCWarnPlugin plugin) {

        this.plugin = plugin;
    }

    public Warning addWarning(String player, String punisher, Location location, Reason reason) {

        Warning warning = new Warning(player, punisher, reason, DateUtil.getCurrentDateString(), location);
        Database.getTable(PointsTable.class).addPoints(warning);
        openWarnings.put(player, warning);
        plugin.getBanManager().checkPlayer(player);
        plugin.getBungeeManager().sendMessage(Bukkit.getOnlinePlayers()[0], new PlayerGetWarningMessage(player, reason.getName()));
//        RCWarn.INST.postThreads();
        if(Bukkit.getPlayer(player) != null) {
            informPlayer(Bukkit.getPlayer(player), warning);
        }

        return warning;
    }

    public void setOpenWarnings(List<Warning> warnings) {
        openWarnings.clear();
        for(Warning warning : warnings) {
            openWarnings.put(warning.getPlayer(), warning);
        }
    }

    public Warning getOpenWarning(String player) {
        return openWarnings.get(player);
    }

    public void removeOpenWarning(String player) {
        openWarnings.remove(player);
    }

    public void informPlayer(Player player, Warning warning) {
        String detail = "keine Details";
        if(warning.getReason().getDetail() != null && warning.getReason().getDetail().length() > 0) {
            detail = warning.getReason().getDetail();
        }
        int points = Database.getTable(PointsTable.class).getAllPoints(player.getName());
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if(warning.getReason().getPoints() > -1)
            player.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Du wurdest verwarnt!");
        else
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.ITALIC + "Du wurdest gelobt!");
        player.sendMessage(ChatColor.YELLOW + "Grund: " + ChatColor.RED + warning.getReason().getName() + " (" + detail + ")");
        player.sendMessage(ChatColor.YELLOW + "Punkte: " + ChatColor.RED + warning.getReason().getPoints() +
                " (Nächster Ban: " + points + "/" + RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().getNextBanLevel(points).getPoints() + ")");
        if(warning.getReason().getPoints() > -1)
            player.sendMessage(ChatColor.RED + "Gebe " + ChatColor.WHITE + "/rcconfirm" + ChatColor.RED + " ein um die Warnung zur Kenntnis zu nehmen!");
        else
            player.sendMessage(ChatColor.GREEN + "Gebe " + ChatColor.WHITE + "/rcconfirm" + ChatColor.RED + " ein um das Lob zur Kenntnis zu nehmen!");
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            new QueuedCommand(player, this, "warningAccept", player.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void informPlayer(Player player) {

        Warning warning = getOpenWarning(player.getName());
        if(warning != null) {
            informPlayer(player, warning);
        }
    }

    public void warningAccept(String player) {
        Database.getTable(PointsTable.class).setAccepted(player);
        RaidCraft.LOGGER.info("Warning accepted by player: " + player);
        if(Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.YELLOW + "Warnung akzeptiert! Du kannst nun weiter spielen!");
        }
    }
}