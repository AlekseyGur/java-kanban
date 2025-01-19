package ru.alexgur.kanban.service.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import ru.alexgur.kanban.service.TaskManager;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void processGet(HttpExchange httpExchange) throws IOException {
        try {
            sendJson(httpExchange, taskManager.getPrioritizedTasks());
        } catch (Exception e) {
            sendStatus(httpExchange, 200);
        }
    }
}
