package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
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

        private final RCWarnPlugin module;

        public NestedAdminCommands(RCWarnPlugin module) {

            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads config and shit"
        )
        @CommandPermissions("rcwarn.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            RaidCraft.getComponent(RCWarnPlugin.class).reload();
            sender.sendMessage(ChatColor.GREEN + "RCWarn wurde neu geladen!");
        }
    }
}

