package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.database.TBans;
import de.raidcraft.rcwarn.database.TPoints;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.CommandUtil;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Philip
 */
public class BansInfoCommand {

    public static final int NUMBER_PRINTED_BANS = 10;

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public BansInfoCommand(RCWarnPlugin module) {
    }

    @Command(
            aliases = {"bans"},
            desc = "Show the bans by player",
            flags = "i"
    )
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        String playerName = sender.getName();
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

        OfflinePlayer playerData = CommandUtil.grabPlayer(playerName);
        UUID playerId = playerData.getUniqueId();
        int points = TPoints.getAllPoints(playerId);

        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        sender.sendMessage(ChatColor.GREEN + "Die letzen Bans von " + playerName + ":");
        sender.sendMessage(ChatColor.YELLOW + "Punkte/Nächster Ban: " + ChatColor.RED + points + "/" + RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().getNextBanLevel(points).getPoints());

        // get all warnings
        long cuttentTime = System.currentTimeMillis();
        List<Ban> allBans = TBans.getBans(playerId);

        if (allBans.size() == 0) {
            sender.sendMessage(ChatColor.GREEN + "Du hast noch keine Bans!");
        } else {
            // sort bans
            SortedMap<Long, Ban> orderedBans = new TreeMap<>();
            for (Ban ban : allBans) {
                orderedBans.put(cuttentTime - DateUtil.getTimeStamp(ban.getDate()), ban);
            }

            int i = 0;
            for (Map.Entry<Long, Ban> entry : orderedBans.entrySet()) {
                i++;
                if (i > NUMBER_PRINTED_BANS) break;

                Ban ban = entry.getValue();

                String strike = "";
                if (ban.isExpired()) {
                    strike = ChatColor.STRIKETHROUGH.toString();
                }

                String expiration = "Unban Request";
                if (ban.isTemporary()) {
                    expiration = ban.getExpiration();
                }

                sender.sendMessage(strike + ban.getDate() + " " +
                        ChatColor.YELLOW + strike + " bis " + ChatColor.WHITE + strike + expiration + ChatColor.RED + strike + " Pt.: " + ban.getPoints());
            }
        }
        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}

