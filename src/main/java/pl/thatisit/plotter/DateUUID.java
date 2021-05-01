package pl.thatisit.plotter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class DateUUID {
    private final String value;
    private final String datePart;
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private DateUUID(String value, String datePart) {
        this.value = value;
        this.datePart = datePart;
    }

    public static DateUUID randomDateUUID() {
        return new DateUUID(UUID.randomUUID().toString().substring(14), LocalDateTime.now().format(dateTimeFormatter));
    }

    @Override
    public String toString() {
       return datePart + "-" + value;
    }
}
