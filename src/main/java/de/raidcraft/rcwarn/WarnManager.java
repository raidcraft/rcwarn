package de.raidcraft.rcwarn;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.rcwarn.database.TPoints;
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
import java.util.UUID;

/**
 * @author Philip
 */
public class WarnManager {

    private RCWarnPlugin plugin;
    private Map<UUID, Warning> openWarnings = new HashMap<>();

    public WarnManager(RCWarnPlugin plugin) {

        this.plugin = plugin;
    }

    public Warning addWarning(UUID player, String punisher, Location location, Reason reason) {

        Warning warning = new Warning(player, punisher, reason, DateUtil.getCurrentDateString(), location);
        TPoints.addPoints(warning);
        openWarnings.put(player, warning);
        plugin.getBanManager().checkPlayer(player);
        // send warning to all servers
        if(Bukkit.getOnlinePlayers().size() > 0) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                plugin.getBungeeManager().sendMessage(online,
                        new PlayerGetWarningMessage(player, reason.getName()));
                //        RCWarn.INST.postThreads();
                break;
            }
            if (Bukkit.getPlayer(player) != null) {
                informPlayer(Bukkit.getPlayer(player), warning);
            }
        }

        return warning;
    }

    public void setOpenWarnings(List<Warning> warnings) {
        openWarnings.clear();
        for (Warning warning : warnings) {
            openWarnings.put(warning.getPlayerId(), warning);
        }
    }

    public Warning getOpenWarning(UUID player) {
        return openWarnings.get(player);
    }

    public void removeOpenWarning(UUID player) {
        openWarnings.remove(player);
    }

    public void informPlayer(Player player, Warning warning) {
        String detail = "keine Details";
        if (warning.getReason().getDetail() != null && warning.getReason().getDetail().length() > 0) {
            detail = warning.getReason().getDetail();
        }
        int points = TPoints.getAllPoints(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (warning.getReason().getPoints() > -1) {
            player.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Du wurdest verwarnt!");
        } else {
            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.ITALIC + "Du wurdest gelobt!");
        }
        player.sendMessage(ChatColor.YELLOW + "Grund: " + ChatColor.RED + warning.getReason().getName() + " (" + detail + ")");
        player.sendMessage(ChatColor.YELLOW + "Punkte: " + ChatColor.RED + warning.getReason().getPoints() +
                " (Nächster Ban: " + points + "/" + RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().getNextBanLevel(points).getPoints() + ")");
        if (warning.getReason().getPoints() > -1) {
            player.sendMessage(ChatColor.RED + "Gebe " + ChatColor.WHITE + "/rcconfirm" + ChatColor.RED + " ein um die Warnung zur Kenntnis zu nehmen!");
        } else {
            player.sendMessage(ChatColor.GREEN + "Gebe " + ChatColor.WHITE + "/rcconfirm" + ChatColor.RED + " ein um das Lob zur Kenntnis zu nehmen!");
        }
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            new QueuedCommand(player, this, "warningAccept", player.getUniqueId());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void informPlayer(Player player) {

        Warning warning = getOpenWarning(player.getUniqueId());
        if (warning != null) {
            informPlayer(player, warning);
        }
    }

    // called via reflection of QueuedCommand
    public void warningAccept(UUID player) {
        TPoints.setAcceptedFlag(player);
        RaidCraft.LOGGER.info("Warning accepted by player: " + player);
        if (Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.YELLOW + "Warnung akzeptiert! Du kannst nun weiter spielen!");
        }
    }
}