package de.raidcraft.rcwarn.multiworld;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcmultiworld.BungeeManager;
import de.raidcraft.rcmultiworld.bungeecord.messages.BungeeMessage;
import de.raidcraft.rcmultiworld.bungeecord.messages.MessageName;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.Reason;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
@MessageName("PLAYER_GET_WARNING_MESSAGE")
public class PlayerGetWarningMessage extends BungeeMessage {

    private String player;
    private Reason reason;

    public PlayerGetWarningMessage(String player, String reasonName) {

        this.player = player;
        this.reason = Reason.getReason(reasonName);
    }

    @Override
    protected String encode() {
        return player + BungeeManager.DELIMITER + reason.getName();
    }

    @Override
    public void process() {

        RaidCraft.getComponent(RCWarnPlugin.class).reload();

        Player victim = Bukkit.getPlayer(player);
        if(victim != null) {
            RaidCraft.getComponent(RCWarnPlugin.class).getWarnManager().informPlayer(victim);
        }
        if(reason.getPoints() > 0) {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + player + " hat eine Verwarnung erhalten (" + reason.getName() + ")!");
        }
        else {
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + player + " hat ein Lob erhalten (" + reason.getName() + ")!");
        }
    }
}
