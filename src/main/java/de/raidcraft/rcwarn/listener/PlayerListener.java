package de.raidcraft.rcwarn.listener;

import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.WarnManager;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Warning;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Philip
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsynchPreLogin(AsyncPlayerPreLoginEvent event) {
        Ban ban = Database.getTable(BansTable.class).getBan(event.getName());
        // no or old ban
        if(ban == null || ban.isExpired()) {
            Database.getTable(BansTable.class).unban(event.getName());
            BanManager.INST.checkPlayer(event.getName());
            return;
        }
        BanManager.INST.kickBannedPlayer(event, ban);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Warning warning = WarnManager.INST.getOpenWarning(event.getPlayer().getName());
        if(warning == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        int points = Database.getTable(PointsTable.class).getAllPoints(player.getName());
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        player.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Du wurdest verwarnt!");
        player.sendMessage(ChatColor.YELLOW + "Grund: " + ChatColor.RED + warning.getReason().getName() + " (" + warning.getReason().getDetail() + ")");
        player.sendMessage(ChatColor.YELLOW + "Punkte: " + ChatColor.RED + warning.getReason().getPoints() +
                " (Nächster Ban: " + points + "/" + BanManager.INST.getNextBanLevel(warning.getReason().getPoints()).getPoints() + ")");
        player.sendMessage(ChatColor.RED + "Gebe " + ChatColor.WHITE + "/rcconfirm" + ChatColor.RED + " ein um die Warnung zur Kenntnis zu nehmen!");
        player.sendMessage(ChatColor.YELLOW + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            new QueuedCommand(event.getPlayer(), this, "warningAccept", player.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void warningAccept(String player) {
        Database.getTable(PointsTable.class).setAccepted(player);
        WarnManager.INST.removeOpenWarning(player);
        if(Bukkit.getPlayer(player) != null) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.YELLOW + "Warnung akzeptiert! Du kannst nun weiter spielen!");
        }
    }

}
