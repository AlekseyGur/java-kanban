// package ru.yandex.javacourse.schedule.http.handler;
package ru.alexgur.kanban.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.alexgur.kanban.adapters.DurationTypeAdapter;
import ru.alexgur.kanban.adapters.LocalDateTimeTypeAdapter;
import ru.alexgur.kanban.adapters.LocalTimeTypeAdapter;
import ru.alexgur.kanban.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BaseHttpHandler implements HttpHandler {
    public TaskManager taskManager;
    Gson gson = getGson();

    BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default:
                writeToUser(exchange, "Данный метод не предусмотрен");
        }
        sendStatus(exchange, 404);
    }

    public void processGet(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    }

    public void processPost(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    }

    public void processDelete(HttpExchange exchange) throws IOException {
        sendNotAllowed(exchange);
    }

    public void writeToUser(HttpExchange exchange, String text) throws IOException {
        sendNotAllowed(exchange);
    }

    public void sendNotAllowed(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(405, 0);
        httpExchange.close();
    }

    public void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    public void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(406, 0);
        httpExchange.close();
    }

    public void sendStatus(HttpExchange httpExchange, int status) throws IOException {
        httpExchange.sendResponseHeaders(status, 0);
        httpExchange.close();
    }

    public void sendError(HttpExchange httpExchange, int status) throws IOException {
        httpExchange.sendResponseHeaders(status, 0);
        httpExchange.close();
    }

    public <T> void sendJson(HttpExchange httpExchange, T obj) throws IOException {
        Gson gson = getGson();
        String json = gson.toJson(obj);
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
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