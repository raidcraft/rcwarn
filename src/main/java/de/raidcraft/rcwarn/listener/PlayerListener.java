package de.raidcraft.rcwarn.listener;

import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.WarnManager;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Warning;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Philip
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Ban ban = Database.getTable(BansTable.class).getBan(event.getPlayer().getName());
        // no or old ban
        if(ban == null || ban.isExpired()) {
            BanManager.INST.checkPlayer(event.getPlayer().getName());
            return;
        }
        BanManager.INST.kickBannedPlayer(event.getPlayer().getName(), ban);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Warning warning = WarnManager.INST.getOpenWarning(event.getPlayer().getName());
        if(warning == null) {
            return;
        }

        Player player = event.getPlayer();

        player.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~");
        player.sendMessage(ChatColor.RED + "Du wurdest verwarnt!");
        player.sendMessage(ChatColor.RED + "Grund: " + warning.getReason().getName() + " (" + warning.getReason().getDetail() + ")");
        player.sendMessage(ChatColor.RED + "Punkte: " + warning.getReason().getPoints() +
                "(Nächste Ban-Stufe: " + BanManager.INST.getNextBanLevel(warning.getReason().getPoints()) + ")");
        player.sendMessage(ChatColor.RED + "Gebe /rcaccept ein um die Warnung zur Kentniss zu nehmen!");
        player.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~");

        QueuedCommand queuedCommand = new QueuedCommand(event.getPlayer(), this, "warningAccept", player);
    }

    public void warningAccept(Player player) {
        Database.getTable(PointsTable.class).setAccepted(player.getName());
        player.sendMessage(ChatColor.YELLOW + "Warnung akzeptiert! Du kannst nun weiter spielen!");
    }

}
