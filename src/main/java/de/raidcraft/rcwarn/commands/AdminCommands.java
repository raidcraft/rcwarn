package de.raidcraft.rcwarn.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.api.database.Database;
import de.raidcraft.rcwarn.RCWarn;
import de.raidcraft.rcwarn.database.ReasonsTable;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Philip
 */
public class AdminCommands {

    public AdminCommands(RCWarn module) {

    }

    @Command(
            aliases = {"rcwarn"},
            desc = "Admin command"
    )
    @NestedCommand(NestedAdminCommands.class)
    public void rcwarn(CommandContext context, CommandSender sender) throws CommandException {
    }


    public static class NestedAdminCommands {

        private final RCWarn module;

        public NestedAdminCommands(RCWarn module) {

            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads config and shit"
        )
        @CommandPermissions("rcwarn.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            RCWarn.INST.reload();
            Database.getTable(ReasonsTable.class).addAllReasons();
            sender.sendMessage(ChatColor.GREEN + "Das Loot-Plugin wurde neugeladen!");
        }
    }
}

