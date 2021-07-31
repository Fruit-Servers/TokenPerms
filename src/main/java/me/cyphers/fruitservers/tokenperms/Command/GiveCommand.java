package me.cyphers.fruitservers.tokenperms.Command;

import me.cyphers.fruitservers.tokenperms.Command.Factory.CommandExecutor;
import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Token.TokenParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GiveCommand extends CommandExecutor {

    private final PlayerMessenger pm;

    public GiveCommand(Plugin plugin) {
        super(plugin);
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!testArgs(sender, args)) return;
        // Player
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            pm.msg(sender, "errorMessages.playerNotFound");
            return;
        }

        // Token Type
        String tokenName = args[2];
        if (!getPlugin().getTokenRegister().getTokenNames().contains(tokenName)) {
            pm.msg(sender, "errorMessages.tokenNotFound", "%token%", tokenName);
            return;
        }

        // Duration - value
        int duration;
        try {
            duration = Integer.parseInt(args[3]);
        } catch (IllegalArgumentException e) {
            pm.msg(sender, "errorMessages.invalidNumber", "%number%", args[3]);
            return;
        }

        // Duration - unit
        ChronoUnit unit;
        try {
            unit = ChronoUnit.valueOf(args[4].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            pm.msg(sender, "errorMessages.invalidTimeUnit");
            return;
        }

        // Amount
        int amount;
        try {
            amount = Integer.parseInt(args[5]);
            if (amount < 0 || amount > 64) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            pm.msg(sender, "errorMessages.invalidNumber", "%number%", args[5]);
            return;
        }

        // Player inventory space
        if (player.getInventory().firstEmpty() == -1) {
            pm.msg(sender, "errorMessages.noSpaceInInventory");
            return;
        }

        // Token Display name
        String displayName = getPlugin().getSettings().getTokenDisplayName(tokenName);
        Material material = getPlugin().getSettings().getTokenMaterial(tokenName);
        List<String> lore = getPlugin().getSettings().getTokenLore(tokenName);
        boolean hasGlow = getPlugin().getSettings().getTokenGlow(tokenName);

        ItemStack token = getPlugin().getTokenRegister().constructToken(material, displayName, lore, duration, unit, amount, hasGlow);

        player.getInventory().addItem(token);

        pm.msg(player, "tokenMessages.tokenAddedToInventory", "%token%", tokenName);

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!hasPermission(sender)) return Collections.emptyList();
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 2:
                return null;
            case 3:
                return new ArrayList<>(getPlugin().getTokenRegister().getTokenNames());
            case 4:
            case 6:
                for (int i = 1; i < 10; i++) {
                    list.add(Integer.toString(i));
                }
                return list;
            case 5:
                return TokenParser.chronoUnits();
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
        return "give";
    }

    @Override
    public String getPermission() {
        return "tokenperms.give";
    }

    @Override
    public int getMinArgLength() {
        return 6;
    }
}
