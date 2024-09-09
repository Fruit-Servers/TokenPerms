package me.cyphers.fruitservers.tokenperms.Command.Factory;

import java.util.List;

import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class CommandExecutor {
    private final Plugin plugin;

    private final PlayerMessenger pm;

    public CommandExecutor(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    public abstract void execute(CommandSender paramCommandSender, String[] paramArrayOfString);

    public boolean testArgs(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            this.pm.msg(sender, "errorMessages.noPermission");
            return false;
        }
        if (args.length < getMinArgLength()) {
            this.pm.msg(sender, "errorMessages.invalidCommandLength");
            return false;
        }
        if (mustBePlayer() && !(sender instanceof org.bukkit.entity.Player)) {
            this.pm.msg(sender, "errorMessages.mustBePlayer");
            return false;
        }
        return true;
    }

    public abstract List<String> onTabComplete(@NotNull CommandSender paramCommandSender, @NotNull Command paramCommand, @NotNull String paramString, @NotNull String[] paramArrayOfString);

    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(getPermission());
    }

    public abstract boolean mustBePlayer();

    public abstract String getKeyword();

    public abstract String getPermission();

    public abstract int getMinArgLength();
}
