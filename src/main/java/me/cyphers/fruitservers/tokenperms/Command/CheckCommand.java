package me.cyphers.fruitservers.tokenperms.Command;

import me.cyphers.fruitservers.tokenperms.Command.Factory.TargettedCommandExecutor;
import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CheckCommand extends TargettedCommandExecutor {

    private final PlayerMessenger pm;

    public CheckCommand(Plugin plugin) {
        super(plugin);
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @Override
    public void executeSelfTarget(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            pm.msg(sender, "errorMessages.mustBePlayer");
            return;
        }

        Player player = (Player) sender;

        // Get the list of schedules for the player
        List<TokenSchedule> schedules = getPlugin().getTokenRegister().getSchedules(player.getUniqueId());

        // No active tokens
        if (schedules.size() == 0) {
            pm.msg(sender, "tokenMessages.noActiveTokens", "%player%", player.getName());
            return;
        }

        // Initial Message for the check
        pm.msg(sender, "tokenMessages.initialCheckMessage", "%player%", player.getName());

        // User has a token active
        for (TokenSchedule schedule : schedules) {
            sendScheduleMessage(schedule, sender);
        }

    }

    @Override
    public void executeOtherTarget(CommandSender sender, String[] args) {
        // If the user is checking themselves
        if (args.length == 1) {
            this.executeSelfTarget(sender, args);
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            pm.msg(sender, "errorMessages.playerNotFound"); return;
        }

        // Get the list of schedules for the player
        List<TokenSchedule> schedules = getPlugin().getTokenRegister().getSchedules(player.getUniqueId());

        // No active tokens
        if (schedules.size() == 0) {
            pm.msg(sender, "tokenMessages.noActiveTokens", "%player%", player.getName());
            return;
        }

        // Initial Message for the check
        pm.msg(sender, "tokenMessages.initialCheckMessage", "%player%", player.getName());

        // User has a token active
        for (TokenSchedule schedule : schedules) {
            sendScheduleMessage(schedule, sender);
        }

    }

    private void sendScheduleMessage(TokenSchedule schedule, CommandSender target) {
        String timeLeftFormat = schedule.getHumanReadableTimeLeft();
        pm.msg(target, "tokenMessages.checkMessage", "%token%", schedule.getTokenName(), "%timeLeft%", timeLeftFormat);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!hasPermission(sender)) return Collections.emptyList();
        if (args.length == 2 && sender.hasPermission(getOtherTargetPermission())) return null;
        else return Collections.emptyList();
    }

    @Override
    public String getOtherTargetPermission() {
        return "tokenperms.check";
    }

    @Override
    public String getSelfTargetPermission() {
        return "tokenperms.check.others";
    }

    @Override
    public boolean mustBePlayer() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "check";
    }

    @Override
    public int getMinArgLength() {
        return 1;
    }
}
