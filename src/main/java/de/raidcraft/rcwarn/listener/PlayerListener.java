package de.raidcraft.rcwarn.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

/**
 * @author Philip
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsynchPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        Ban ban = RaidCraft.getTable(BansTable.class).getBan(uuid);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getName());
        if (offlinePlayer != null && offlinePlayer.isBanned() && ban == null) {
            plugin.getWarnManager().addWarning(uuid,
                    "RCWarn",
                    null,
                    new Reason("Altban", 100, 0).setDetail("Der Spieler war bereits gebannt!"));
            plugin.getBanManager().checkPlayer(uuid);
            return;
        }

        // no or old ban
        if (ban == null || ban.isExpired()) {
            Database.getTable(BansTable.class).unban(uuid);
            plugin.getBanManager().checkPlayer(uuid);
            return;
        }
        plugin.getBanManager().kickBannedPlayer(event, ban);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {

        RCWarnPlugin plugin = RaidCraft.getComponent(RCWarnPlugin.class);
        Warning warning = plugin.getWarnManager().getOpenWarning(event.getPlayer().getUniqueId());
        if (warning == null) {
            return;
        }

        event.setCancelled(true);
        plugin.getWarnManager().informPlayer(event.getPlayer(), warning);
    }
}
