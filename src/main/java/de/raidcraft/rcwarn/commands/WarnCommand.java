package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.rcwarn.RCWarn;
import de.raidcraft.rcwarn.WarnManager;
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
public class WarnCommand {

    private Map<String, Warning> lastWarnings = new HashMap<>();

    public WarnCommand(RCWarn module) {
    }

    @Command(
            aliases = {"warn", "ban"},
            desc = "Warn command",
            flags = "f"
    )
    @CommandPermissions("rcwarn.warn")
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        if(context.argsLength() < 2) {
            throw new CommandException("Zu wenig Argumente!");
        }

        //check player
        String player = context.getString(0);
        RCPlayer rcplayer = RCWarn.INST.getPlayer(player);
        if(rcplayer == null) {
            throw new CommandException("Der angegebene Spieler ist unbekannt! (Verschrieben?)");
        }
        player = rcplayer.getDisplayName();

        if(rcplayer.hasPermission("rcwarn.ignore")) {
            throw new CommandException("Dieser Spieler kann nicht verwarnt werden! (Mod / Admin?)");
        }

        if(!context.hasFlag('f')) {
            Warning lastWarning = lastWarnings.get(player);

            if(lastWarning != null && DateUtil.getTimeStamp(lastWarning.getDate()) + RCWarn.INST.config.warningCooldown > System.currentTimeMillis()) {
                sender.sendMessage(ChatColor.RED + "Der Spieler hat in den letzen Minuten erst eine Verwarnung erhalten:");
                sender.sendMessage(ChatColor.RED + "Grund: " + lastWarning.getReason().getName() + " (" + lastWarning.getReason().getDetail() + ")");
                sender.sendMessage(ChatColor.RED + "Nutze '/warn <Spieler> <Grund> [Detail] -f' um den Spieler trotzdem zu warnen!");
                return;
            }
            else if (lastWarning != null) {
                lastWarnings.remove(player);
            }
        }
        else if(!sender.hasPermission("rcwarn.warn.force")) {
            throw new CommandException("Du darfst Warnungen nicht erzwingen!");
        }

        //check reason
        Reason reason = Reason.getReason(context.getString(1));
        if(reason != null) {
            reason = reason.clone();
        }
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

        if(!sender.hasPermission("rcwarn.warn.unlimited") && reason.getPoints() > RCWarn.INST.config.supporterMaxWarnPoints) {
            throw new CommandException("Supporter dürfen nur Warnungen mit max. " + RCWarn.INST.config.supporterMaxWarnPoints + " Punkten aussprechen!");
        }

        sender.sendMessage(ChatColor.GREEN + "Du hast '" + ChatColor.YELLOW + player + ChatColor.GREEN + "' verwarnt! "
                + ChatColor.YELLOW + "(" + ChatColor.RED + reason.getName() + ChatColor.YELLOW + ")");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + player + " wurde verwarnt (" + reason.getName() + ")!");

        Location location = null;
        if(sender instanceof Player) {
            location = ((Player) sender).getLocation();
        }
        lastWarnings.put(player, WarnManager.INST.addWarning(player, sender.getName(), location, reason));
    }
}

