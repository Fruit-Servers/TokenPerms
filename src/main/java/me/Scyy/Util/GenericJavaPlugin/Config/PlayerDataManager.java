package me.Scyy.Util.GenericJavaPlugin.Config;

import me.Scyy.Util.GenericJavaPlugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager implements ConfigManager {

    private final Map<UUID, PlayerDataFile> playerData;

    private final Plugin plugin;

    public PlayerDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
    }

    public void loadPlayerData(UUID uuid) {
        if (playerData.containsKey(uuid)) return;
        playerData.put(uuid, new PlayerDataFile(this, uuid));
    }

    public void unloadPlayerData(UUID uuid) {
        if (!playerData.containsKey(uuid)) return;
        playerData.remove(uuid);
    }

    public PlayerDataFile getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    @Override
    public void reloadConfigs() throws Exception {
        for (UUID uuid : playerData.keySet()) {
            playerData.get(uuid).reloadConfig();
        }
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
