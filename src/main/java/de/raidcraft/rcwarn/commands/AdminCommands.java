package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.rcwarn.RCWarnPlugin;
import org.bukkit.ChatColor;
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

            plugin.getBanManager().checkPlayer(context.getString(0));
            sender.sendMessage(ChatColor.GREEN + "Der Spieler " + context.getString(0) + " wurde überprüft!");
        }
    }
}

