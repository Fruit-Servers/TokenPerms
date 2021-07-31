package me.cyphers.fruitservers.tokenperms.Event;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.Config.Settings;
import me.cyphers.fruitservers.tokenperms.GUI.ConfirmGUI;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEventHandler implements Listener {
    private final Plugin plugin;
    private final Settings settings;
    private final PlayerMessenger pm;

    public PlayerEventHandler(Plugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.pm = plugin.getConfigManager().getPlayerMessenger();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
            if (heldItem.getType() != Material.AIR) {
                TokenSchedule schedule = this.plugin.getTokenRegister().deconstructToken(heldItem, event.getPlayer());
                if (!TokenSchedule.isInvalid(schedule)) {
                    if (this.plugin.getSettings().isTokenEnabled(schedule.getTokenName())) {
                        if (!this.plugin.getSettings().getWhitelistedWorlds().contains(event.getPlayer().getWorld().getName())) {
                            this.pm.msg(event.getPlayer(), "tokenMessages.invalidWorldForToken");
                        } else {
                            ItemStack heldItemClone = heldItem.clone();
                            heldItemClone.setAmount(1);
                            ConfirmGUI gui = new ConfirmGUI(null, this.plugin, event.getPlayer(), heldItemClone, heldItem, schedule);
                            gui.open(event.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Set<String> tokenNamesToRemove = this.plugin.getConfigManager().getTokenScheduleData().getPlayerRemoveList(playerUUID);
        CommandSender console = Bukkit.getConsoleSender();
        if (tokenNamesToRemove.size() != 0) {
            for (String tokenName : tokenNamesToRemove) {
                List<String> commandsToRun = this.settings.getTokenDisabledPlayerRejoinCommands(tokenName, player.getName());
                String permissionToRemove = this.settings.getTokenPermission(tokenName);
                if (commandsToRun.size() != 0) {
                    for (String command : commandsToRun) {
                        Bukkit.dispatchCommand(console, command);
                    }
                    if (permissionToRemove != null) {
                        this.plugin.getPermissionBase().playerRemove(player, permissionToRemove);
                    }

                }

            }

            try {
                this.plugin.getConfigManager().getTokenScheduleData().removePlayerRemoveList(playerUUID);
            } catch (Exception ex) {
                this.plugin.getLogger().warning("Error modifying schedule data! Player " + player.getName() + " may experience weird behaviour");
            }

        }
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        List<TokenSchedule> schedules = this.plugin.getTokenRegister().getSchedules(player.getUniqueId());
        if (schedules.size() != 0) {
            World destination = player.getWorld();
            List<String> whitelistedWorlds = this.plugin.getSettings().getWhitelistedWorlds();
            CommandSender console = Bukkit.getConsoleSender();
            if (!whitelistedWorlds.contains(destination.getName())) {
                schedules.forEach(tokenSchedule -> {
                    String tokenName = tokenSchedule.getTokenName();
                    List<String> commandsToRun = this.settings.getTokenDisabledPlayerOnlineCommands(tokenName, player.getName());
                    String permissionToRemove = this.settings.getTokenPermission(tokenName);
                    if (commandsToRun.size() != 0) commandsToRun.forEach(command -> Bukkit.dispatchCommand(console, command));
                    if (permissionToRemove != null) this.plugin.getPermissionBase().playerRemove(player, permissionToRemove);
                    tokenSchedule.setPaused(true);
                });

                this.pm.msg(player, "tokenMessages.tokensPausedAndDisabled");
            } else {
                final boolean[] resumedTokens = {false};
                schedules.forEach(tokenSchedule -> {
                    tokenSchedule.setPaused(false);
                    if (!resumedTokens[0]) resumedTokens[0] = true;
                });

                if (resumedTokens[0]) {
                    pm.msg(player, "tokenMessages.tokenResumed");
                }
            }

        }
    }
}
