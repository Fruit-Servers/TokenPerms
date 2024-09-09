package me.cyphers.fruitservers.tokenperms.Token;

import me.cyphers.fruitservers.tokenperms.Config.Settings;
import me.cyphers.fruitservers.tokenperms.Plugin;
import me.cyphers.fruitservers.tokenperms.Util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class TokenRegister {

    private final Plugin plugin;

    private final Set<String> tokenNames;

    private final Map<UUID, TokenSchedule> schedules;

    public TokenRegister(Plugin plugin) {
        this.plugin = plugin;
        Settings settings = plugin.getSettings();

        this.tokenNames = settings.getTokenNames();
        Map<UUID, TokenSchedule> tempSchedules;
        try {
            tempSchedules = plugin.getConfigManager().getTokenScheduleData().loadSchedules();
        } catch (Exception e) {
            tempSchedules = new HashMap<>();
            plugin.getLogger().warning("An issue occured while loading saved token schedules!");
            e.printStackTrace();
        }
        this.schedules = tempSchedules;

        // Start the clock for tracking schedules - checks every second
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {

            // Don't loop through schedules if empty
            if (schedules.size() == 0) return;
            for (UUID scheduleKey : new HashSet<>(schedules.keySet())) {

                TokenSchedule schedule = schedules.get(scheduleKey);

                // Token is paused, treat it differently
                if (schedule.isPaused()) {
                    schedule.addTime(1000);
                    continue;
                }

                // Schedule must end
                if (Instant.now().isAfter(schedule.getEnd())) {
                    this.endSchedule(schedule.getScheduleID(), schedule.getPlayer());
                }
                if (Instant.now().isAfter(schedule.getEnd().minus(60, ChronoUnit.SECONDS)) && !schedule.isFirstWarningSent()) {
                    this.sendWarning(schedule, "tokenUseMessages." + schedule.getTokenName() + "warning1");
                    schedule.setFirstWarningSent(true);
                }
                if (Instant.now().isAfter(schedule.getEnd().minus(10, ChronoUnit.SECONDS)) && !schedule.isSecondWarningSent()) {
                    this.sendWarning(schedule, "tokenUseMessages." + schedule.getTokenName() + "warning2");
                    schedule.setSecondWarningSent(true);
                }
            }
        }, 20, 20);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Set<String> getTokenNames() {
        return tokenNames;
    }

    public Collection<TokenSchedule> getSchedules() {
        return schedules.values();
    }

    public UUID startSchedule(TokenSchedule schedule, Player player) {
        // check if there is an existing schedule
        List<TokenSchedule> existing = getSchedules(player.getUniqueId());
        if (existing.size() != 0) {
            // filter for any matches based on token name
            existing.stream().filter(schedule1 -> schedule.getTokenName().equalsIgnoreCase(schedule1.getTokenName()))
                    // If a match is found, add the current time to the new schedule and remove the current schedule
                    .findFirst().ifPresent(schedule1 -> {
                        schedule.addTime(schedule1.getMillisLeft());
                        schedules.remove(schedule1.getScheduleID());
            });
        }
        schedule.start();
        this.schedules.put(schedule.getScheduleID(), schedule);
        String permission = plugin.getSettings().getTokenPermission(schedule.getTokenName());
        plugin.getPermissionBase().playerAdd(player, permission);
        return schedule.getScheduleID();
    }

    public List<TokenSchedule> getAllSchedules() {
        return new ArrayList<>(schedules.values());
    }

    public List<TokenSchedule> getSchedules(UUID player) {
        return schedules.values().stream()
                .filter((schedule -> schedule.getPlayer().equals(player)))
                .collect(Collectors.toList());
    }

    public void endSchedule(UUID scheduleUUID, UUID player) {
        TokenSchedule schedule = this.schedules.get(scheduleUUID);
        if (schedule == null) return;
        String permission = plugin.getSettings().getTokenPermission(schedule.getTokenName());
        this.endSchedulePermission(player, permission, schedule.getTokenName());
        this.schedules.remove(scheduleUUID);
    }

    public void sendWarning(TokenSchedule schedule, String warningPath) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(schedule.getPlayer());
            if (player == null) return;
            plugin.getConfigManager().getPlayerMessenger().msg(player, warningPath);
        });
    }

    public void endSchedulePermission(UUID playerID, String permission, String tokenName) {
        Player player = Bukkit.getPlayer(playerID);
        // Player is online
        if (player != null) {

            // Remove the permission
            plugin.getPermissionBase().playerRemove(player, permission);

            // Run the commands to remove if present
            List<String> commandsToRun = plugin.getSettings().getTokenDisabledPlayerOnlineCommands(tokenName, player.getName());
            if (!commandsToRun.isEmpty()) {
                CommandSender console = Bukkit.getConsoleSender();
                for (String command : commandsToRun) {
                    Bukkit.dispatchCommand(console, command);
                }
            }

        // Player is not online, add a listener to remove the perm when they next rejoin
        } else {
            try {
                plugin.getConfigManager().getTokenScheduleData().addPlayerRemoveList(playerID, tokenName);
            // This should never occur, but if it does log it to console
            } catch (Exception e) {
                plugin.getLogger().warning("Could not save player data to config! Player: " + playerID + ", Permission: " + permission);
                e.printStackTrace();
            }
        }
    }

    public ItemStack constructToken(Material material, String tokenName, List<String> lore, int duration, ChronoUnit unit, int amount, boolean hasGlow) {
        ItemBuilder builder = new ItemBuilder(material).name(tokenName);
        String extraLine = "&r&8Duration: " + duration + " " + unit.toString();
        if (hasGlow) builder.enchant();
        return builder.lore(extraLine).lore(lore).amount(amount).build();
    }

    /**
     * Creates a TokenSchedule from a given token that has been used by player.
     * Is effectively the same as a player 'using' the token
     * @param token the item to deconstruct
     * @return a TokenSchedule with no set start or end date - this is set with {@link TokenSchedule#start()}
     */
    public TokenSchedule deconstructToken(ItemStack token, Player player) {
        ItemMeta itemMeta = token.getItemMeta();

        UUID scheduleID = UUID.randomUUID();

        // Token Duration
        List<String> lore = itemMeta.getLore();
        if (lore == null || lore.size() < 1) return TokenSchedule.invalidSchedule();
        String loreLine = lore.get(0);
        String[] durationData = loreLine.split(" ");
        if (durationData.length != 3) return TokenSchedule.invalidSchedule();

        // Parse the token data
        int duration = TokenParser.parseDuration(durationData[1]);
        ChronoUnit durationUnit = TokenParser.parseChronoUnit(durationData[2]);

        // (Unit in milli form) * (duration of token)
        long tokenDurationMillis = durationUnit.getDuration().toMillis() * duration;

        // Token player
        UUID playerUUID = player.getUniqueId();

        // Token Name
        String tokenName = plugin.getSettings().getTokenName(token.getType());
        if (tokenName == null || tokenName.equalsIgnoreCase("")) return TokenSchedule.invalidSchedule();

        // Create the token
        return new TokenSchedule(scheduleID, tokenDurationMillis, playerUUID, tokenName);

    }

}
