package me.cyphers.fruitservers.tokenperms.Command;

import me.cyphers.fruitservers.tokenperms.Command.Factory.CommandExecutor;
import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CancelCommand extends CommandExecutor {

    private final PlayerMessenger pm;

    public CancelCommand(Plugin plugin) {
        super(plugin);
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(testArgs(sender, args))) return;

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            pm.msg(sender, "errorMessages.playerNotFound");
            return;
        }

        List<TokenSchedule> schedules = getPlugin().getTokenRegister().getSchedules(player.getUniqueId());

        Optional<TokenSchedule> optional = schedules.stream().filter(schedule -> schedule.getTokenName().toLowerCase(Locale.ROOT)
                .equals(args[2].toLowerCase(Locale.ROOT))).findFirst();

        if (optional.isPresent()) {
            TokenSchedule schedule = optional.get();
            getPlugin().getTokenRegister().endSchedule(schedule.getScheduleID(), player.getUniqueId());
            pm.msg(sender, "tokenMessages.tokenCancelled", "%token%", args[2].toLowerCase(Locale.ROOT),
                    "%player%", player.getName());
        } else {
            pm.msg(sender, "tokenMessages.noScheduleFound", "%token%", args[2].toLowerCase(Locale.ROOT),
                    "%player%", player.getName());
        }

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!hasPermission(sender)) return Collections.emptyList();
        switch (args.length) {
            case 2:
                return null;
            case 3:
                return new ArrayList<>(getPlugin().getTokenRegister().getTokenNames());
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public boolean mustBePlayer() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "cancel";
    }

    @Override
    public String getPermission() {
        return "tokenPerms.cancel";
    }

    @Override
    public int getMinArgLength() {
        return 3;
    }
}
