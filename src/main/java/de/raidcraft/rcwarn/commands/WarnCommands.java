package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.rcwarn.RCWarn;
import de.raidcraft.rcwarn.WarnManager;
import de.raidcraft.rcwarn.util.Reason;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Philip
 */
public class WarnCommands {

    public WarnCommands(RCWarn module) {
    }

    @Command(
            aliases = {"warn", "ban"},
            desc = "Warn command"
    )
    @CommandPermissions("rcwarn.warn")
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        if(context.argsLength() < 2) {
            throw new CommandException("Zu wenig Argumente!");
        }

        //check player
        String player = context.getString(0);
        //TODO implement

        //check reason
        Reason reason = Reason.getReason(context.getString(1));
        if(reason == null) {
            sender.sendMessage(ChatColor.RED + "Unbekannter Grund! Verfügbare Gründe:");
            String reasonNames = "";
            for(String reasonName : Reason.getAllReasonNames()) {
                reasonNames += ChatColor.RED + reasonName + ChatColor.YELLOW + ", ";
            }
            sender.sendMessage(ChatColor.RED + reasonNames);
            return;
        }
        if(context.argsLength() > 2) {
            reason.setDetail(context.getJoinedStrings(2));
        }

        WarnManager.INST.addWarning(player, reason);
        sender.sendMessage(ChatColor.GREEN + "Der Spieler '" + ChatColor.YELLOW + player + ChatColor.GREEN + "' wurde verwarnt!");
    }
}

