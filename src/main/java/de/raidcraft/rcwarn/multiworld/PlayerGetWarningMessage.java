package de.raidcraft.rcwarn.multiworld;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcmultiworld.BungeeManager;
import de.raidcraft.rcmultiworld.bungeecord.messages.BungeeMessage;
import de.raidcraft.rcmultiworld.bungeecord.messages.MessageName;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Philip Urban
 */
@MessageName("PLAYER_GET_WARNING_MESSAGE")
public class PlayerGetWarningMessage extends BungeeMessage {

    private UUID playerId;
    private Reason reason;

    public PlayerGetWarningMessage(UUID playerId, String reasonName) {

        this.playerId = playerId;
        this.reason = Reason.getReason(reasonName);
    }

    @Override
    protected String encode() {
        return UUIDUtil.getNameFromUUID(playerId) + BungeeManager.DELIMITER + reason.getName();
    }

    @Override
    public void process() {

        RaidCraft.getComponent(RCWarnPlugin.class).reload();

        Player victim = Bukkit.getPlayer(playerId);
        if (victim != null) {
            RaidCraft.getComponent(RCWarnPlugin.class).getWarnManager().informPlayer(victim);
        }
        if (reason.getPoints() > 0) {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + UUIDUtil.getNameFromUUID(playerId) +
                    " hat eine Verwarnung erhalten (" + reason.getName() + ")!");
        } else {
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + UUIDUtil.getNameFromUUID(playerId) +
                    " hat ein Lob erhalten (" + reason.getName() + ")!");
        }
    }
}
