package me.cyphers.fruitservers.tokenperms.Config;

import me.cyphers.fruitservers.tokenperms.Plugin;

public class SimpleConfigManager implements ConfigManager {

    private final Plugin plugin;

    private final Settings settings;
    private final PlayerMessenger playerMessenger;
    private final TokenScheduleData tokenScheduleData;

    /**
     * Load all configs in
     * @param plugin the plugin to get Plugin data folder references
     */
    public SimpleConfigManager(Plugin plugin) {
        this.plugin = plugin;
        this.settings = new Settings(this);
        this.playerMessenger = new PlayerMessenger(this);
        this.tokenScheduleData = new TokenScheduleData(this);
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

    public TokenScheduleData getTokenScheduleData() {
        return tokenScheduleData;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
