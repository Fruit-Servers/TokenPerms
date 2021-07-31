package me.cyphers.fruitservers.tokenperms.Command;

import me.cyphers.fruitservers.tokenperms.Command.Factory.CommandExecutor;
import me.cyphers.fruitservers.tokenperms.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends CommandExecutor {
    public ReloadCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!testArgs(sender, args)) return;
        getPlugin().reload(sender);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean mustBePlayer() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "tokenperms.reload";
    }

    @Override
    public int getMinArgLength() {
        return 1;
    }
}
