package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.rcwarn.database.PointsTable;
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
public class WarningsInfoCommand {

    public static final int NUMBER_PRINTED_WARNINGS = 10;

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public WarningsInfoCommand(RCWarnPlugin module) {
    }

    @Command(
            aliases = {"warnings", "warns"},
            desc = "Show the warnings by player",
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
        int points = Database.getTable(PointsTable.class).getAllPoints(playerId);

        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        sender.sendMessage(ChatColor.GREEN + "Die letzen Verwarnungen von " + playerName + ":");
        sender.sendMessage(ChatColor.YELLOW + "Punkte/Nächster Ban: " + ChatColor.RED + points + "/" + RaidCraft.getComponent(RCWarnPlugin.class).getBanManager().getNextBanLevel(points).getPoints());

        // get all warnings
        long currentTime = System.currentTimeMillis();
        List<Warning> allWarnings = Database.getTable(PointsTable.class).getAllWarnings(playerId);

        if (allWarnings.size() == 0) {
            sender.sendMessage(ChatColor.GREEN + "Du hast noch keine Verwarnungen!");
        } else {
            // sort warnings
            SortedMap<Long, Warning> orderedWarnings = new TreeMap<>();
            for (Warning warning : allWarnings) {
                orderedWarnings.put(currentTime - DateUtil.getTimeStamp(warning.getDate()), warning);
            }

            int i = 0;
            for (Map.Entry<Long, Warning> entry : orderedWarnings.entrySet()) {
                i++;
                if (i > NUMBER_PRINTED_WARNINGS) break;

                Warning warning = entry.getValue();

                String strike = "";
                if (warning.isExpired()) {
                    strike = ChatColor.STRIKETHROUGH.toString();
                }

                String detail = "keine Details";
                if (warning.getReason().getDetail() != null && warning.getReason().getDetail().length() > 0) {
                    detail = warning.getReason().getDetail();
                }

                sender.sendMessage(strike + warning.getDate() + " " +
                        ChatColor.YELLOW + strike + warning.getReason().getName() + " - " + detail +
                        " - " + ChatColor.RED + strike + warning.getReason().getPoints());
            }
        }
        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}

