package pl.thatisit.plotter.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

public final class DateUUID {
    private final String value;
    private final String datePart;
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    static final String pattern = "^\\d{8}-\\d{6}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$";

    private DateUUID(String value, String datePart) {
        this.value = value;
        this.datePart = datePart;
    }

    public static DateUUID randomDateUUID() {
        return new DateUUID(UUID.randomUUID().toString().substring(14), LocalDateTime.now().format(dateTimeFormatter));
    }

    public static boolean isDateUUID(String value) {
        return Pattern.compile(pattern).matcher(value).find();
    }

    @Override
    public String toString() {
        return datePart + "-" + value;
    }
}
