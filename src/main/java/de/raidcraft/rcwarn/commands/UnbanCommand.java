package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.RCWarn;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Reason;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip
 */
public class UnbanCommand {

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public UnbanCommand(RCWarn module) {
    }

    @Command(
            aliases = {"unban", "pardon"},
            desc = "Unban command"
    )
    @CommandPermissions("rcwarn.unban")
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        if(context.argsLength() < 1) {
            throw new CommandException("Zu wenig Argumente!");
        }

        //check player
        String player = context.getString(0);
        RCPlayer rcplayer = RCWarn.INST.getPlayer(player);
        if(rcplayer == null) {
            throw new CommandException("Der angegebene Spieler ist unbekannt! (Verschrieben?)");
        }
        player = rcplayer.getDisplayName();

        Ban ban = Database.getTable(BansTable.class).getBan(player);
        if(ban == null || ban.isExpired()) {
            throw new CommandException("Der Spieler '" + player + "' ist nicht gebannt!");
        }

        if(!ban.isTemporary()) {
            Location location = null;
            if(sender instanceof Player) {
                location = ((Player) sender).getLocation();
            }
            int points = Database.getTable(PointsTable.class).getAllPoints(player);
            points -= BanManager.INST.getHighestBanLevel().getPoints() - 2;
            Database.getTable(PointsTable.class)
                    .addPoints(
                            new Warning(player, sender.getName(),
                                    new Reason("Unban", -points, 0), DateUtil.getCurrentDateString(), location));
        }
        Database.getTable(BansTable.class).unban(player);

        sender.sendMessage(ChatColor.GREEN + "Du hast '" + ChatColor.YELLOW + player + ChatColor.GREEN + "' entbannt!");
        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + player + " wurde entbannt!");
    }
}

