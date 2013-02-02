package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.rcwarn.BanManager;
import de.raidcraft.rcwarn.RCWarn;
import de.raidcraft.rcwarn.database.BansTable;
import de.raidcraft.rcwarn.database.PointsTable;
import de.raidcraft.rcwarn.util.Ban;
import de.raidcraft.rcwarn.util.Warning;
import de.raidcraft.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * @author Philip
 */
public class BansInfoCommand {

    public static final int NUMBER_PRINTED_BANS = 10;

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public BansInfoCommand(RCWarn module) {
    }

    @Command(
            aliases = {"bans"},
            desc = "Show the bans by player",
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
        sender.sendMessage(ChatColor.GREEN + "Die letzen Bans von " + player + "");
        sender.sendMessage(ChatColor.YELLOW + "Punkte/Nächster Ban: " + ChatColor.RED + points + "/" + BanManager.INST.getNextBanLevel(points).getPoints());

        // get all warnings
        long cuttentTime = System.currentTimeMillis();
        List<Ban> allBans = Database.getTable(BansTable.class).getBans(player);

        if(allBans.size() == 0) {
            sender.sendMessage(ChatColor.GREEN + "Du hast noch keine Bans!");
        }
        else {
            // sort bans
            SortedMap<Long, Ban> orderedBans = new TreeMap<>();
            for(Ban ban : allBans) {
                orderedBans.put(cuttentTime - DateUtil.getTimeStamp(ban.getDate()), ban);
            }

            int i = 0;
            for(Map.Entry<Long, Ban> entry : orderedBans.entrySet()) {
                i++;
                if(i > NUMBER_PRINTED_BANS) break;

                Ban ban = entry.getValue();

                String strike = "";
                if(ban.isExpired())
                    strike = ChatColor.STRIKETHROUGH.toString();

                String expiration = "Unban Request";
                if(ban.isTemporary()) {
                    expiration = ban.getExpiration();
                }

                sender.sendMessage(strike + ban.getDate() + " " +
                        ChatColor.YELLOW + strike + " bis " + ChatColor.WHITE + strike + expiration + ChatColor.RED + strike + " Pt.: " + ban.getPoints());
            }
        }
        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}

