package me.Scyy.Util.GenericJavaPlugin.Config;

import me.Scyy.Util.GenericJavaPlugin.Plugin;

public class SimpleConfigManager implements ConfigManager {

    private final Plugin plugin;

    private final PlayerMessenger playerMessenger;
    private final Settings settings;

    /**
     * Load all configs in
     * @param plugin the plugin to get Plugin data folder references
     */
    public SimpleConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.playerMessenger = new PlayerMessenger(this);
        this.settings = new Settings(this);
    }

    /**
     * Reloads all ConfigFiles registered to this handler
     */
    @Override
    public void reloadConfigs() throws Exception {
        playerMessenger.reloadConfig();
        settings.reloadConfig();
    }

    /**
     * Get the Player Messenger ConfigFile
     * @return the Player Messenger
     */
    public PlayerMessenger getPlayerMessenger() {
        return playerMessenger;
    }

    /**
     * Get the default Settings ConfigFile
     * @return the Settings
     */
    public Settings getSettings() {
        return settings;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
