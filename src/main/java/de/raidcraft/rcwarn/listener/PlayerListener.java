package de.raidcraft.rcwarn.listener;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.WarnManager;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Warning;
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
        WarnManager.INST.informPlayer(event.getPlayer(), warning);
    }
}
