package me.cyphers.fruitservers.tokenperms.Command.Factory;

import me.cyphers.fruitservers.tokenperms.Command.*;
import me.cyphers.fruitservers.tokenperms.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommandFactory implements TabExecutor {

    private final Plugin plugin;

    private final CancelCommand cancelCommand;
    private final CheckCommand checkCommand;
    private final GiveCommand giveCommand;
    private final ListCommand listCommand;
    private final ReloadCommand reloadCommand;


    public CommandFactory(Plugin plugin) {
        this.plugin = plugin;
        this.cancelCommand = new CancelCommand(plugin);
        this.checkCommand = new CheckCommand(plugin);
        this.giveCommand = new GiveCommand(plugin);
        this.listCommand = new ListCommand(plugin);
        this.reloadCommand = new ReloadCommand(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 0) {
            for (String line : plugin.getSplashText()) {
                sender.sendMessage(line);
            }
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "give":
                giveCommand.execute(sender, args);
                return true;
            case "check":
                checkCommand.execute(sender, args);
                return true;
            case "list":
                listCommand.execute(sender, args);
                return true;
            case "cancel":
                cancelCommand.execute(sender, args);
                return true;
            case "reload":
                reloadCommand.execute(sender, args);
                return true;
            default:
                plugin.getConfigManager().getPlayerMessenger().msg(sender, "errorMessages.commandNotFound");
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if (cancelCommand.hasPermission(sender)) list.add("cancel");
            if (checkCommand.hasPermission(sender)) list.add("check");
            if (giveCommand.hasPermission(sender)) list.add("give");
            if (listCommand.hasPermission(sender)) list.add("list");
            if (reloadCommand.hasPermission(sender)) list.add("reload");
            return list;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "give":
                return giveCommand.onTabComplete(sender, command, s, args);
            case "check":
                return checkCommand.onTabComplete(sender, command, s, args);
            case "list":
                return listCommand.onTabComplete(sender, command, s, args);
            case "cancel":
                return cancelCommand.onTabComplete(sender, command, s, args);
            case "reload":
                return reloadCommand.onTabComplete(sender, command, s, args);
            default:
                return Collections.emptyList();
        }
    }
}
