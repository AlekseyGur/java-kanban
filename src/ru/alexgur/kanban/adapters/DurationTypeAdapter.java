package ru.alexgur.kanban.adapters;

import java.io.IOException;
import java.time.Duration;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        int durationMinutes = Integer.valueOf(jsonReader.nextString());
        return Duration.ofMinutes(durationMinutes);
    }
}