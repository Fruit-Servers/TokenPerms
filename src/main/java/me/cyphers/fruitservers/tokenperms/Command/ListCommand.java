package me.cyphers.fruitservers.tokenperms.Command;

import me.cyphers.fruitservers.tokenperms.Command.Factory.CommandExecutor;
import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import me.cyphers.fruitservers.tokenperms.Command.Factory.CommandExecutor;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ListCommand extends CommandExecutor {

    private final PlayerMessenger pm;

    public ListCommand(Plugin plugin) {
        super(plugin);
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!testArgs(sender, args)) return;

        List<TokenSchedule> schedules = getPlugin().getTokenRegister().getAllSchedules();

        pm.msg(sender, "tokenMessages.initialListMessage");

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            for (TokenSchedule schedule : schedules) {

                OfflinePlayer player = Bukkit.getOfflinePlayer(schedule.getPlayer());
                String playerName = player.getName();
                String tokenName = schedule.getTokenName();
                String timeLeftFormat = schedule.getHumanReadableTimeLeft();

                pm.msg(sender, "tokenMessages.listMessage", "%player%", playerName,
                        "%token%", tokenName, "%timeLeft%", timeLeftFormat);
            }
        });
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.getPermission());
    }

    @Override
    public boolean mustBePlayer() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "tokenPerms.list";
    }

    @Override
    public int getMinArgLength() {
        return 1;
    }
}
