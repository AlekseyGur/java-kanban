package ru.alexgur.kanban.service.handlers;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.TaskManager;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void processGet(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> hist = taskManager.getHistoryManager().getHistory();
            sendJson(httpExchange, hist);
        } catch (Exception e) {
            sendStatus(httpExchange, 200);
        }
    }
}