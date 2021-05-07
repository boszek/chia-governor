package pl.thatisit.plotter.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converter {
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (date, type, serializationContext) ->
                    new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, serializationContext) ->
                    new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .create();

    public static String toJson(Object value) {
        return gson.toJson(value);
    }
}
