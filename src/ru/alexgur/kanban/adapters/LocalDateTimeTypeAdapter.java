package ru.alexgur.kanban.adapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localTime) throws IOException {
        String toConvert = (localTime != null) ? localTime.format(timeFormatter) : "";
        jsonWriter.value(toConvert);
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String resource = jsonReader.nextString();
        return (resource.length() > 0) ? LocalDateTime.parse(resource, timeFormatter) : null;
    }
}