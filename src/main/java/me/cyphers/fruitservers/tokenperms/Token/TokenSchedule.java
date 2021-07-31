package me.cyphers.fruitservers.tokenperms.Token;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class TokenSchedule implements Comparable<TokenSchedule> {

    private final UUID scheduleID;

    private long durationMillis;

    private Instant end;

    private Instant start;

    private final UUID player;

    private final String tokenName;

    private boolean paused;

    private boolean firstWarningSent;

    private boolean secondWarningSent;

    public TokenSchedule(UUID scheduleID, Instant start, Instant end, UUID player, String tokenName, boolean paused) {
        this.scheduleID = scheduleID;
        this.durationMillis = end.toEpochMilli() - start.toEpochMilli();
        this.start = start;
        this.end = end;
        this.player = player;
        this.tokenName = tokenName;
        this.paused = paused;
        this.firstWarningSent = false;
        this.secondWarningSent = false;
    }

    public TokenSchedule(UUID scheduleID, long durationMillis, UUID player, String tokenName) {
        this.scheduleID = scheduleID;
        this.durationMillis = durationMillis;
        this.start = null;
        this.end = null;
        this.player = player;
        this.tokenName = tokenName;
        this.paused = false;
        this.firstWarningSent = false;
        this.secondWarningSent = false;
    }

    public void start() {
        if (this.start != null || this.end != null) {
            throw new IllegalStateException("Cannot start a token that has already started");
        }
        this.start = Instant.now();
        this.end = start.plusMillis(durationMillis);
    }

    public UUID getScheduleID() {
        return scheduleID;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getTokenName() {
        return tokenName;
    }

    public long getMillisLeft() {
        return end.toEpochMilli() - Instant.now().toEpochMilli();
    }

    public void addTime(long millis) {
        this.durationMillis += millis;
        if (end != null) {
            end = start.plusMillis(durationMillis);
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isFirstWarningSent() {
        return firstWarningSent;
    }

    public void setFirstWarningSent(boolean firstWarningSent) {
        this.firstWarningSent = firstWarningSent;
    }

    public boolean isSecondWarningSent() {
        return secondWarningSent;
    }

    public void setSecondWarningSent(boolean secondWarningSent) {
        this.secondWarningSent = secondWarningSent;
    }

    public String getHumanReadableTimeLeft() {

        long timeLeft = getMillisLeft();

        // Days
        long days = timeLeft / ChronoUnit.DAYS.getDuration().toMillis();
        long dayRemaining = timeLeft % ChronoUnit.DAYS.getDuration().toMillis();
        String daysFormat = days + "d ";

        // Hours
        long hours = dayRemaining / ChronoUnit.HOURS.getDuration().toMillis();
        long hourRemaining = dayRemaining % ChronoUnit.HOURS.getDuration().toMillis();
        String hoursFormat = (Long.toString(hours).length() == 2 ? Long.toString(hours) : "0" + hours) + ":";

        // Minutes
        long minutes = hourRemaining / ChronoUnit.MINUTES.getDuration().toMillis();
        long minuteRemaining = hourRemaining % ChronoUnit.MINUTES.getDuration().toMillis();
        String minutesFormat = (Long.toString(minutes).length() == 2 ? Long.toString(minutes) : "0" + minutes) + ":";

        // Seconds
        long seconds = minuteRemaining / ChronoUnit.SECONDS.getDuration().toMillis();
        String secondsFormat = (Long.toString(seconds).length() == 2 ? Long.toString(seconds) : "0" + seconds);

        // Paused state
        if (paused) secondsFormat += " PAUSED";

        return daysFormat + hoursFormat + minutesFormat + secondsFormat;

    }

    @Override
    public int compareTo(@NotNull TokenSchedule schedule) {
        return (int) (schedule.getMillisLeft() - this.getMillisLeft());
    }

    public static TokenSchedule invalidSchedule() {
        return new TokenSchedule(null, -1, null, "");
    }

    public static boolean isInvalid(TokenSchedule schedule) {
        return schedule.durationMillis == -1 || schedule.player == null || schedule.tokenName.equalsIgnoreCase("");
    }
}
