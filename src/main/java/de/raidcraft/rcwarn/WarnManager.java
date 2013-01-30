package de.raidcraft.rcwarn;

import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.database.PointsTable;
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

    public static final WarnManager INST = new WarnManager();

    private Map<String, Warning> openWarnings = new HashMap<>();

    public Warning addWarning(String player, String punisher, Location location, Reason reason) {

        Warning warning = new Warning(player, punisher, reason, DateUtil.getCurrentDateString(), location);
        Database.getTable(PointsTable.class).addPoints(warning);
        openWarnings.put(player, warning);
        BanManager.INST.checkPlayer(player);

        if(Bukkit.getPlayer(player) != null) {
            WarnManager.INST.informPlayer(Bukkit.getPlayer(player), warning);
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
        int points = Database.getTable(PointsTable.class).getAllPoints(player.getName());
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        player.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Du wurdest verwarnt!");
        player.sendMessage(ChatColor.YELLOW + "Grund: " + ChatColor.RED + warning.getReason().getName() + " (" + warning.getReason().getDetail() + ")");
        player.sendMessage(ChatColor.YELLOW + "Punkte: " + ChatColor.RED + warning.getReason().getPoints() +
                " (Nächster Ban: " + points + "/" + BanManager.INST.getNextBanLevel(warning.getReason().getPoints()).getPoints() + ")");
        player.sendMessage(ChatColor.RED + "Gebe " + ChatColor.WHITE + "/rcconfirm" + ChatColor.RED + " ein um die Warnung zur Kenntnis zu nehmen!");
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            new QueuedCommand(player, this, "warningAccept", player.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void warningAccept(String player) {
        Database.getTable(PointsTable.class).setAccepted(player);
        if(Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.YELLOW + "Warnung akzeptiert! Du kannst nun weiter spielen!");
        }
    }
}
