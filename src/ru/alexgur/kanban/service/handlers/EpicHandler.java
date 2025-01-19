package ru.alexgur.kanban.service.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import ru.alexgur.kanban.adapters.EpicTypeToken;
import ru.alexgur.kanban.exceptions.ManagerAddTaskException;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.service.TaskManager;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void processGet(HttpExchange httpExchange) throws IOException {
        String[] arPath = httpExchange.getRequestURI().getPath().split("/");
        if (arPath.length > 3) { // path: /tasks/{id}/subtasks
            try {
                int id = Integer.parseInt(arPath[2]);
                List<SubTask> tasks = taskManager.getEpicSubTasks(id);
                sendJson(httpExchange, tasks);
            } catch (Exception e) {
                sendNotFound(httpExchange);
            }
        } else if (arPath.length > 2) { // path: /tasks/{id}/
            try {
                int id = Integer.parseInt(arPath[2]);
                Epic task = taskManager.getEpic(id);
                sendJson(httpExchange, task);
            } catch (Exception e) {
                sendNotFound(httpExchange);
            }
        } else { // path: /tasks/
            List<Epic> tasks = taskManager.getEpics();
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
            Epic newTask = gson.fromJson(body, new EpicTypeToken().getType());

            if (arPath.length > 2) { // обновлени данных: /tasks/{id}/
                int id = Integer.parseInt(arPath[2]);
                Epic savedTask = taskManager.getEpic(id);
                if (savedTask == null) {
                    sendStatus(httpExchange, 404);
                } else {
                    taskManager.updateEpic(newTask);
                }
            } else {
                taskManager.addEpic(newTask);
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
                taskManager.deleteEpic(id);
                sendStatus(httpExchange, 200);
            } catch (Exception e) {
                sendNotFound(httpExchange);
            }
        }
    }
}
