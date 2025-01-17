// package ru.yandex.javacourse.schedule.http.handler;
package ru.alexgur.kanban.service;

import com.sun.net.httpserver.HttpExchange;

import ru.alexgur.kanban.adapters.DurationTypeAdapter;
import ru.alexgur.kanban.adapters.LocalDateTimeTypeAdapter;
import ru.alexgur.kanban.adapters.LocalTimeTypeAdapter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseHttpHandler {
    public static <T> void sendJson(HttpExchange httpExchange, T obj) throws IOException {
        Gson gson = getGson();
        String json = gson.toJson(obj);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    public static void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    public static void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(406, 0);
        httpExchange.close();
    }

    public static void sendStatus(HttpExchange httpExchange, int status) throws IOException {
        httpExchange.sendResponseHeaders(status, 0);
        httpExchange.close();
    }

    public static void sendError(HttpExchange httpExchange, int status) throws IOException {
        httpExchange.sendResponseHeaders(status, 0);
        httpExchange.close();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        Gson gson = gsonBuilder.create();
        return gson;
    }
}