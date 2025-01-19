package ru.alexgur.kanban.adapters;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalTime localTime) throws IOException {
        String toConvert = (localTime != null) ? localTime.format(timeFormatter) : "";
        jsonWriter.value(toConvert);
    }

    @Override
    public LocalTime read(final JsonReader jsonReader) throws IOException {
        String resource = jsonReader.nextString();
        return (resource.length() > 0) ? LocalTime.parse(resource, timeFormatter) : null;
    }
}