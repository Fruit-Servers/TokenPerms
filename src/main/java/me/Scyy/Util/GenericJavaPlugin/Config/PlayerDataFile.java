package me.Scyy.Util.GenericJavaPlugin.Config;

import java.io.File;
import java.util.UUID;

public class PlayerDataFile extends ConfigFile {

    private final UUID uuid;

    public PlayerDataFile(ConfigManager manager, UUID uuid) {
        super(manager, "player_data" + File.separator + uuid.toString() + ".yml", false);
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }
}
