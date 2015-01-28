package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.rcwarn.RCWarnPlugin;
import de.raidcraft.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author Philip
 */
public class AdminCommands {

    public AdminCommands(RCWarnPlugin module) {

    }

    @Command(
            aliases = {"rcwarn"},
            desc = "Admin command"
    )
    @NestedCommand(NestedAdminCommands.class)
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {
    }


    public static class NestedAdminCommands {

        private final RCWarnPlugin plugin;

        public NestedAdminCommands(RCWarnPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads config and shit"
        )
        @CommandPermissions("rcwarn.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "RCWarn wurde neu geladen!");
        }

        @Command(
                aliases = {"check"},
                desc = "Checks player if he should be banned",
                min = 1,
                usage = "<Player>"
        )
        @CommandPermissions("rcwarn.reload")
        public void check(CommandContext context, CommandSender sender) throws CommandException {
            OfflinePlayer player = CommandUtil.grabPlayer(context.getString(0));
            plugin.getBanManager().checkPlayer(player.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Der Spieler " + context.getString(0) + " wurde überprüft!");
        }
    }
}

