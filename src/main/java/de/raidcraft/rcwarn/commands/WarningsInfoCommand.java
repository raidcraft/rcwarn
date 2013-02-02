package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.RCWarn;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * @author Philip
 */
public class WarningsInfoCommand {

    public static final int NUMBER_PRINTED_WARNINGS = 10;

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public WarningsInfoCommand(RCWarn module) {
    }

    @Command(
            aliases = {"warnings", "warns"},
            desc = "Show the warnings by player",
            flags = "i"
    )
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        String player = sender.getName();
        if(context.argsLength() > 0 && sender.hasPermission("rcwarn.info.other")) {
            if(context.hasFlag('i')) {
                player = context.getString(0);
            }
            else {
                RCPlayer rcplayer = RCWarn.INST.getPlayer(player);
                if(rcplayer == null) {
                    throw new CommandException("Der angegebene Spieler ist angeblich unbekannt! Nutze -i um das zu ignorieren!");
                }
                player = rcplayer.getDisplayName();
            }
        }

        int points = Database.getTable(PointsTable.class).getAllPoints(player);

        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        sender.sendMessage(ChatColor.GREEN + "Die letzen Verwarnungen von " + player + ":");
        sender.sendMessage(ChatColor.YELLOW + "Punkte/Nächster Ban: " + ChatColor.RED + points + "/" + BanManager.INST.getNextBanLevel(points).getPoints());

        // get all warnings
        long cuttentTime = System.currentTimeMillis();
        List<Warning> allWarnings = Database.getTable(PointsTable.class).getAllWarnings(player);

        if(allWarnings.size() == 0) {
            sender.sendMessage(ChatColor.GREEN + "Du hast noch keine Verwarnungen!");
        }
        else {
            // sort warnings
            SortedMap<Long, Warning> orderedWarnings = new TreeMap<>();
            for(Warning warning : allWarnings) {
                orderedWarnings.put(cuttentTime - DateUtil.getTimeStamp(warning.getDate()), warning);
            }

            int i = 0;
            for(Map.Entry<Long, Warning> entry : orderedWarnings.entrySet()) {
                i++;
                if(i > NUMBER_PRINTED_WARNINGS) break;

                Warning warning = entry.getValue();

                String strike = "";
                if(warning.isExpired())
                    strike = ChatColor.STRIKETHROUGH.toString();

                String detail = "keine Details";
                if(warning.getReason().getDetail() != null && warning.getReason().getDetail().length() > 0) {
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

