package me.cyphers.fruitservers.tokenperms.Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings extends ConfigFile {

    /**
     * Create a ConfigFile for the default 'config.yml' file
     * Intended to be used as a read-only file, it is highly recommended that
     *  {@link org.bukkit.configuration.file.YamlConfiguration#set(String, Object)} is not used on this file as comments will be overwritten
     */
    public Settings(ConfigManager manager) {
        super(manager,"config.yml", true);
    }

    public Set<String> getTokenNames() {
        ConfigurationSection section = config.getConfigurationSection("tokens");
        if (section == null) return new HashSet<>();
        return section.getKeys(false);
    }

    public String getTokenDisplayName(String tokenName) {
        return config.getString("tokens." + tokenName + ".name", tokenName);
    }

    public boolean isTokenEnabled(String tokenName) {
        return config.getBoolean("tokens." + tokenName + ".enabled", false);
    }

    public String getTokenPermission(String tokenName) {
        return config.getString("tokens." + tokenName + ".permission");
    }

    public Material getTokenMaterial(String tokenName) {
        String rawMaterial = config.getString("tokens." + tokenName + ".item", "STONE");
        return Material.valueOf(rawMaterial);
    }

    public List<String> getTokenLore(String tokenName) {
        return config.getStringList("tokens." + tokenName + ".lore");
    }

    public boolean getTokenGlow(String tokenName) {
        return config.getBoolean("tokens." + tokenName + ".hasGlow", false);
    }

    public String getTokenActivatedText(String tokenName) {
        String rawText = config.getString("tokens." + tokenName + ".messageWhenActiviated");
        if (rawText == null) return "";
        return ChatColor.translateAlternateColorCodes('&', rawText);
    }

    public List<String> getTokenDisabledPlayerOnlineCommands(String tokenName, String playerName) {
        List<String> rawConsoleCommandList = config.getStringList("tokens." + tokenName + ".disabledOnlineCommands");
        List<String> consoleCommandList = new ArrayList<>();
        if (rawConsoleCommandList.size() == 0) return rawConsoleCommandList;
        for (String command : rawConsoleCommandList) {
            consoleCommandList.add(command.replaceAll("%player%", playerName));
        }
        return consoleCommandList;
    }

    public List<String> getTokenDisabledPlayerRejoinCommands(String tokenName, String playerName) {
        List<String> rawConsoleCommandList = config.getStringList("tokens." + tokenName + ".disabledRejoinCommands");
        List<String> consoleCommandList = new ArrayList<>();
        if (rawConsoleCommandList.size() == 0) return rawConsoleCommandList;
        for (String command : rawConsoleCommandList) {
            consoleCommandList.add(command.replaceAll("%player%", playerName));
        }
        return consoleCommandList;
    }



    public String getTokenName(Material tokenMaterial) {
        ConfigurationSection section = config.getConfigurationSection("tokens");
        if (section == null) {
            plugin.getLogger().warning("Could not find config section 'tokens'");
            return "";
        }
        for (String tokenName : section.getKeys(true)) {
            String rawMaterialName = section.getString(tokenName + ".item");
            if (rawMaterialName == null) continue;
            try {
                Material material = Material.valueOf(rawMaterialName);
                if (material == tokenMaterial) return tokenName;
            } catch (IllegalArgumentException ignored) {
            }
        }
        return "";
    }

    public List<String> getWhitelistedWorlds() {
        return config.getStringList("worldWhitelist");
    }

}
