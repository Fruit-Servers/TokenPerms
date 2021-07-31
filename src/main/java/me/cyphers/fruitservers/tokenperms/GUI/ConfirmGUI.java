package me.cyphers.fruitservers.tokenperms.GUI;

import me.cyphers.fruitservers.tokenperms.Config.PlayerMessenger;
import me.cyphers.fruitservers.tokenperms.GUI.Type.GUI;
import me.cyphers.fruitservers.tokenperms.GUI.Type.InventoryGUI;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import me.cyphers.fruitservers.tokenperms.Util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfirmGUI extends InventoryGUI {

    private final ItemStack token;

    private final ItemStack heldItem;

    private final TokenSchedule schedule;

    /**
     * @param lastGUI The GUI that was open before this one, or <code>null</code> if opened for the first time
     * @param plugin  The main plugin instance
     * @param player  The player that this GUI is being presented to
     */
    public ConfirmGUI(@Nullable GUI<?> lastGUI, @NotNull Plugin plugin, @NotNull Player player, ItemStack displayToken, ItemStack itemUsed, TokenSchedule schedule) {
        super(lastGUI, plugin, player, "&6Confirm Token", 9);
        this.token = displayToken;
        this.heldItem = itemUsed;
        this.schedule = schedule;

        // Fill with blank gray glass panes
        for (int i = 0; i < 9; i++) {
            inventoryItems[i] = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        }

        // Confirm button
        inventoryItems[1] = new ItemBuilder(Material.LIME_DYE).name("&aCONFIRM").build();

        // Token item
        ItemStack newToken = displayToken.clone();
        newToken.setAmount(1);
        inventoryItems[4] = newToken;

        // Deny button
        inventoryItems[7] = new ItemBuilder(Material.RED_DYE).name("&cCANCEL").build();

    }

    @Override
    public @NotNull GUI<?> handleInteraction(InventoryClickEvent event) {

        event.setCancelled(true);

        int clickedSlot = event.getRawSlot();

        // User clicked CONFIRM
        if (clickedSlot == 1) {

            if (heldItem.getAmount() != 1) heldItem.setAmount(heldItem.getAmount() - 1);
            else player.getInventory().setItemInMainHand(null);

            this.close = true;

            getPlugin().getTokenRegister().startSchedule(schedule, player);

            PlayerMessenger pm = getPlugin().getConfigManager().getPlayerMessenger();

            pm.msg(getPlayer(), "tokenMessages.tokenUsed", "%token%", schedule.getTokenName());

            pm.msg(player, "tokenUseMessages." + schedule.getTokenName() + "messageWhenActivated");

            // Broadcast the token being used to all players online with the broadcast permission
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("tokenPerms.viewTokenBroadcast") && !this.player.getUniqueId().equals(player.getUniqueId())) {
                    pm.msg(player, "tokenMessages.broadcastTokenUsed", "%player%", this.player.getName(), "%token%", schedule.getTokenName());
                }
            }

            return new UninteractableGUI(this);

        }

        // User clicked CANCEL
        if (clickedSlot == 7) {

            this.close = true;

            getPlugin().getConfigManager().getPlayerMessenger().msg(getPlayer(), "tokenMessages.tokenDenied",
                    "%token%", schedule.getTokenName());

            return new UninteractableGUI(this);

        }

        return new ConfirmGUI(this, getPlugin(), getPlayer(), token, heldItem, schedule);

    }

    @Override
    public boolean allowPlayerInventoryEdits() {
        return false;
    }

}
