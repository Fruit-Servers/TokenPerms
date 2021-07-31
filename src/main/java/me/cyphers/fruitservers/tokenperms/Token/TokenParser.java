package me.cyphers.fruitservers.tokenperms.Token;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TokenParser {

    public static int parseDuration(String duration) {
        try {
            return Integer.parseInt(duration);
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    public static ChronoUnit parseChronoUnit(String unit) {
        try {
            return ChronoUnit.valueOf(unit.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ChronoUnit.MINUTES;
        }
    }

    public static List<String> chronoUnits() {
        return Arrays.asList("seconds", "minutes", "hours", "days", "weeks");
    }

}
