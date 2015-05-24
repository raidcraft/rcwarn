package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.CommandUtil;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Philip
 */
public class UnbanCommand {

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public UnbanCommand(RCWarnPlugin module) {
    }

    @Command(
            aliases = {"unban", "pardon"},
            desc = "Unban command",
            flags = "i"
    )
    @CommandPermissions("rcwarn.unban")
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        if (context.argsLength() < 1) {
            throw new CommandException("Zu wenig Argumente!");
        }

        //check player
        String playerName = context.getString(0);
        if (context.argsLength() > 0 && sender.hasPermission("rcwarn.info.other")) {
            if (context.hasFlag('i')) {
                playerName = context.getString(0);
            } else {
                Player onlinePlayer = Bukkit.getPlayer(context.getString(0));
                if (onlinePlayer == null) {
                    throw new CommandException("Der angegebene Spieler ist angeblich unbekannt! Nutze -i um das zu ignorieren!");
                }
                playerName = onlinePlayer.getName();
            }
        }

        OfflinePlayer playerData = CommandUtil.grabOfflinePlayer(playerName);
        UUID playerId = playerData.getUniqueId();
        Ban ban = Database.getTable(BansTable.class).getBan(playerId);
        if (ban == null || ban.isExpired()) {
            throw new CommandException("Der Spieler '" + playerName + "' ist nicht gebannt!");
        }

        if (!ban.isTemporary()) {
            Location location = null;
            if (sender instanceof Player) {
                location = ((Player) sender).getLocation();
            }
            int points = Database.getTable(PointsTable.class).getAllPoints(playerId);
            points -= RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().getHighestBanLevel().getPoints() - 2;
            Database.getTable(PointsTable.class)
                    .addPoints(
                            new Warning(playerId, sender.getName(),
                                    new Reason("Unban", -points, 0), DateUtil.getCurrentDateString(), location));
            Database.getTable(PointsTable.class).setAccepted(playerId);
        }
        Database.getTable(BansTable.class).unban(playerId);

        sender.sendMessage(ChatColor.GREEN + "Du hast '" + ChatColor.YELLOW + playerName + ChatColor.GREEN + "' entbannt!");
        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + playerName + " wurde entbannt!");
    }
}

