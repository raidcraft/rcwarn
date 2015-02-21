package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraft;
import de.raidcraft.rcwarn.RCWarnPlugin;
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
public class WarnCommand {

    private Map<UUID, Warning> lastWarnings = new HashMap<>();

    public WarnCommand(RCWarnPlugin module) {
    }

    @Command(
            aliases = {"warn", "ban", "praise"},
            desc = "Warn command",
            flags = "fi"
    )
    @CommandPermissions("rcwarn.warn")
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {

        if (context.argsLength() < 2) {
            throw new CommandException("Zu wenig Argumente! /warn <Spieler> <Grund> [Detail]");
        }

        //check player
        String playerName = context.getString(0);
        OfflinePlayer playerData = CommandUtil.grabOfflinePlayer(playerName);
        if (!context.hasFlag('i')) {
            Player onlinePlayer = CommandUtil.grabPlayer(playerName);
            if (onlinePlayer == null) {
                throw new CommandException("Der angegebene Spieler ist angeblich unbekannt! Nutze -i um das zu ignorieren!");
            }
            if (onlinePlayer.hasPermission("rcwarn.ignore")) {

                if (sender instanceof Player) {
                    ((Player) sender).kickPlayer("Du hast versucht einen Mod oder Admin zu verwarnen!");
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + sender.getName() + " hat versucht einen Mod/Admin zu verwarnen und wurde gekickt! Haha!");
                    lastWarnings.put(((Player) sender).getUniqueId(),
                            RaidCraft.getComponent(RCWarnPlugin.class).getWarnManager().addWarning(
                                    ((Player) sender).getUniqueId(),
                                    "RCWarn",
                                    ((Player) sender).getLocation(),
                                    Reason.getReason("Putschversuch")));
                }
                throw new CommandException("Dieser Spieler kann nicht verwarnt werden! Mod / Admin?");
            }
            playerName = onlinePlayer.getName();
        }

        if (!context.hasFlag('f')) {
            Warning lastWarning = lastWarnings.get(playerData.getUniqueId());

            if (lastWarning != null && DateUtil.getTimeStamp(lastWarning.getDate()) + RaidCraft.getComponent(RCWarnPlugin.class).getConfig().warningCooldown > System.currentTimeMillis()) {
                sender.sendMessage(ChatColor.RED + "Der Spieler hat in den letzen Minuten erst eine Verwarnung erhalten:");
                sender.sendMessage(ChatColor.RED + "Grund: " + lastWarning.getReason().getName() + " (" + lastWarning.getReason().getDetail() + ")");
                sender.sendMessage(ChatColor.RED + "Nutze '/warn <Spieler> <Grund> [Detail] -f' um den Spieler trotzdem zu warnen!");
                return;
            } else if (lastWarning != null) {
                lastWarnings.remove(playerData.getUniqueId());
            }
        } else if (!sender.hasPermission("rcwarn.warn.force")) {
            throw new CommandException("Du darfst Warnungen nicht erzwingen!");
        }

        //check reason
        Reason reason = Reason.getReason(context.getString(1));
        if (reason != null) {
            reason = reason.clone();
        }
        if (reason == null) {
            sender.sendMessage(ChatColor.RED + "Unbekannter Grund! Verfügbare Gründe:");
            String reasonNames = "";
            for (String reasonName : Reason.getAllReasonNames()) {
                reasonNames += ChatColor.RED + reasonName + ChatColor.YELLOW + ", ";
            }
            sender.sendMessage(ChatColor.RED + reasonNames);
            return;
        }
        if (context.argsLength() > 2) {
            reason.setDetail(context.getJoinedStrings(2));
        }

        if (reason.getPoints() > 0) {
            sender.sendMessage(ChatColor.GREEN + "Du hast '" + ChatColor.YELLOW + playerName + ChatColor.GREEN + "' verwarnt! "
                    + ChatColor.YELLOW + "(" + ChatColor.RED + reason.getName() + ChatColor.YELLOW + ")");
            Bukkit.broadcastMessage(ChatColor.DARK_RED + playerName + " hat eine Verwarnung erhalten (" + reason.getName() + ")!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Du hast '" + ChatColor.YELLOW + playerName + ChatColor.GREEN + "' gelobt! "
                    + ChatColor.YELLOW + "(" + ChatColor.RED + reason.getName() + ChatColor.YELLOW + ")");
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + playerName + " hat ein Lob erhalten (" + reason.getName() + ")!");
        }

        Location location = null;
        if (sender instanceof Player) {
            location = ((Player) sender).getLocation();
        }
        lastWarnings.put(playerData.getUniqueId(),
                RaidCraft.getComponent(RCWarnPlugin.class).getWarnManager().addWarning(
                        playerData.getUniqueId(),
                        sender.getName(),
                        location,
                        reason));
    }
}

