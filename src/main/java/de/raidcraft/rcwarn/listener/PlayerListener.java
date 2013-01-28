package de.raidcraft.rcwarn.listener;

import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.util.Ban;
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

    }

}
