package ru.alexgur.kanban.service.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import ru.alexgur.kanban.adapters.TaskTypeToken;
import ru.alexgur.kanban.exceptions.ManagerAddTaskException;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.TaskManager;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void processGet(HttpExchange httpExchange) throws IOException {
        String[] arPath = httpExchange.getRequestURI().getPath().split("/");
        if (arPath.length > 2) { // path: /tasks/{id}/
            try {
                int id = Integer.parseInt(arPath[2]);
                Task task = taskManager.getTask(id);
                sendJson(httpExchange, task);
            } catch (Exception e) {
                sendNotFound(httpExchange);
            }
        } else { // path: /tasks/
            List<Task> tasks = taskManager.getTasks();
            if (tasks != null) {
                sendJson(httpExchange, tasks);
            } else {
                sendNotFound(httpExchange);
            }
        }
    }

    @Override
    public void processPost(HttpExchange httpExchange) throws IOException {
        try {
            InputStream inputStream = httpExchange.getRequestBody();
            String[] arPath = httpExchange.getRequestURI().getPath().split("/");
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task newTask = gson.fromJson(body, new TaskTypeToken().getType());

            if (arPath.length > 2) { // обновлени данных: /tasks/{id}/
                int id = Integer.parseInt(arPath[2]);
                Task savedTask = taskManager.getTask(id);
                if (savedTask == null) {
                    sendStatus(httpExchange, 404);
                } else {
                    taskManager.updateTask(newTask);
                }
            } else {
                taskManager.addTask(newTask);
                sendStatus(httpExchange, 201);
            }
        } catch (ManagerAddTaskException e) {
            sendHasInteractions(httpExchange);
        } catch (Exception e) {
            sendError(httpExchange, 500);
        }
    }

    @Override
    public void processDelete(HttpExchange httpExchange) throws IOException {
        String[] arPath = httpExchange.getRequestURI().getPath().split("/");
        if (arPath.length > 2) { // path: /tasks/{id}/
            try {
                int id = Integer.parseInt(arPath[2]);
                taskManager.deleteTask(id);
                sendStatus(httpExchange, 200);
            } catch (Exception e) {
                sendNotFound(httpExchange);
            }
        }
    }
}