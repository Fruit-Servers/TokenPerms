package me.cyphers.fruitservers.tokenperms.Config;

import me.cyphers.fruitservers.tokenperms.Token.TokenSchedule;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.util.*;

public class TokenScheduleData extends ConfigFile {

    public TokenScheduleData(ConfigManager manager) {
        super(manager, "schedules.yml", true);
    }

    public Map<UUID, TokenSchedule> loadSchedules() {

        // Create an empty list
        Map<UUID, TokenSchedule> schedules = new HashMap<>();

        ConfigurationSection section = config.getConfigurationSection("schedules");

        // Check if the section is present
        if (section == null) return schedules;

        // Check if there is any data in the schedule array
        if (section.getKeys(false).size() == 0) return schedules;

        for (String key : section.getKeys(false)) {

            UUID scheduleID = UUID.randomUUID();
            Instant start = Instant.ofEpochMilli(section.getLong(key + ".start"));
            Instant end = Instant.ofEpochMilli(section.getLong(key + ".end"));
            UUID player = UUID.fromString(section.getString(key + ".player"));
            String tokenName = section.getString(key + ".tokenName");
            boolean paused = section.getBoolean(key + ".paused");

            TokenSchedule schedule = new TokenSchedule(scheduleID, start, end, player, tokenName, paused);
            schedules.put(scheduleID, schedule);
        }

        return schedules;

    }

    public void saveSchedules(Collection<TokenSchedule> schedules) throws Exception {

        // Clear out any existing data
        this.config.set("schedules", null);

        // Save the schedules to config
        int i = 0;
        for (TokenSchedule schedule : schedules) {
            this.config.set("schedules." + i + ".start", schedule.getStart().toEpochMilli());
            this.config.set("schedules." + i + ".end", schedule.getEnd().toEpochMilli());
            this.config.set("schedules." + i + ".player", schedule.getPlayer().toString());
            this.config.set("schedules." + i + ".tokenName", schedule.getTokenName());
            this.config.set("schedules." + i + ".paused", schedule.isPaused());
            i++;
        }

        // Update the config file
        config.save(configFile);

    }

    public void addPlayerRemoveList(UUID player, String tokenName) throws Exception {
        this.config.set("tokenRemoveList." + player.toString() + "." + tokenName, true);
        this.config.save(configFile);
    }

    public Set<String> getPlayerRemoveList(UUID player) {
        ConfigurationSection playerSection = this.config.getConfigurationSection("tokenRemoveList." + player.toString());
        if (playerSection == null) return new HashSet<>();
        return playerSection.getKeys(false);
    }

    public void removePlayerRemoveList(UUID playerUUID) throws Exception {
        this.config.set("tokenRemoveList." + playerUUID.toString(), null);
        this.config.save(configFile);
    }
}
