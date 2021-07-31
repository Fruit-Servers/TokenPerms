package me.cyphers.fruitservers.tokenperms;

import me.cyphers.fruitservers.tokenperms.Command.Factory.CommandFactory;
import me.cyphers.fruitservers.tokenperms.Config.SimpleConfigManager;
import me.cyphers.fruitservers.tokenperms.Config.Settings;
import me.cyphers.fruitservers.tokenperms.Event.PlayerEventHandler;
import me.cyphers.fruitservers.tokenperms.GUI.Type.InventoryGUI;
import me.cyphers.fruitservers.tokenperms.Token.TokenRegister;
import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// H1 requirements
// TODO --== FEATURES ==--
// DONE create google doc for commands/permissions/features
// TEST - TODO change command on token end to new format
// command on end becomes list
// split command on end to command on end if player offline and rejoin
// and command on end when player online

// URGENT TODO - test interacting with the gui - token should only get removed on confirm click

// TODO --== TESTING ==--
// DONE able to use seconds/hours/days for schedule lengths
// TODO tokens not being removed from inventory at 1 token
// TODO schedules are saved and loaded from config correctly
// TODO worlds have an effect on schedule state - pausing and disabling tokens on unwhitelisted worlds
// TODO schedules correctly pause and it displays on check/list

public class Plugin extends JavaPlugin {

    private boolean successfulEnable;

    private SimpleConfigManager configManager;

    private TokenRegister tokenRegister;

    private Permission permissionBase;

    @Override
    public void onEnable() {

        this.successfulEnable = false;

        if (!setUpPermissions()) {
            this.getLogger().severe("Could not find Vault Dependency! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register the Config Manager
        this.configManager = new SimpleConfigManager(this);

        this.tokenRegister = new TokenRegister(this);

        // Register the event listener
        Bukkit.getPluginManager().registerEvents(new PlayerEventHandler(this), this);

        // Register the inventory listener
        Bukkit.getPluginManager().registerEvents(InventoryGUI.getListener(), this);

        // Register the Command Factory
        CommandFactory commandFactory = new CommandFactory(this);
        this.getCommand("tokenperm").setExecutor(commandFactory);
        this.getCommand("tokenperm").setTabCompleter(commandFactory);

        this.successfulEnable = true;

    }

    @Override
    public void onDisable() {

        if (!successfulEnable) return;

        Collection<TokenSchedule> schedules = tokenRegister.getSchedules();
        try {
            this.getConfigManager().getTokenScheduleData().saveSchedules(schedules);
        } catch (Exception e) {
            getLogger().warning("Error saving schedule data to config - schedule data may be lost!");
            e.printStackTrace();
        }
    }

    private boolean setUpPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        try {
            RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp == null) return false;
            this.permissionBase = rsp.getProvider();
            return true;
        } catch (NoClassDefFoundError e) {
            this.permissionBase = null;
            return false;
        }
    }

    /**
     * Reload all configs registered by the {@link SimpleConfigManager} for this plugin
     * @param sender Output for messages
     */
    public void reload(CommandSender sender) {
        try {
            sender.sendMessage("Reloading...");
            configManager.reloadConfigs();
            sender.sendMessage("Successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("Error reloading! Check console for logs!");
            e.printStackTrace();
        }
    }

    /**
     * Get the Settings for this plugin, each defined in config.yml
     * @return the Settings
     */
    public Settings getSettings() {
        return configManager.getSettings();
    }

    /**
     * Provides a bit of information about the plugin
     * @return the splash text
     */
    public List<String> getSplashText() {
        StringBuilder authors = new StringBuilder();
        for (String author : this.getDescription().getAuthors()) {
            authors.append(author).append(", ");
        }
        authors.delete(authors.length() - 1, authors.length());
        return Arrays.asList(
                "TokenPerms v" + this.getDescription().getVersion(),
                "Built by " + authors
        );
    }

    /**
     * @return the Config Manager
     */
    public SimpleConfigManager getConfigManager() {
        return configManager;
    }

    public Permission getPermissionBase() {
        return permissionBase;
    }

    public TokenRegister getTokenRegister() {
        return tokenRegister;
    }
}
