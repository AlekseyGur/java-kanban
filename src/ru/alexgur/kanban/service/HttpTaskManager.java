package ru.alexgur.kanban.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.alexgur.kanban.adapters.EpicTypeToken;
import ru.alexgur.kanban.adapters.SubTaskTypeToken;
import ru.alexgur.kanban.adapters.TaskTypeToken;
import ru.alexgur.kanban.exceptions.ManagerAddTaskException;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;

import java.nio.charset.StandardCharsets;

public class HttpTaskManager extends HttpTaskServer {
    public HttpTaskManager(TaskManager taskManager) {
        super(taskManager);
    }

    public static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod().trim();

            if (method.contains("POST")) { // path: /tasks/
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
            } else if (method.contains("DELETE")) {
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
            } else if (method.contains("GET")) {
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
            sendStatus(httpExchange, 404);
        }
    }

    public static class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod().trim();

            if (method.contains("POST")) { // path: /tasks/
                try {
                    InputStream inputStream = httpExchange.getRequestBody();
                    String[] arPath = httpExchange.getRequestURI().getPath().split("/");
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    SubTask newTask = gson.fromJson(body, new SubTaskTypeToken().getType());

                    if (arPath.length > 2) { // обновлени данных: /tasks/{id}/
                        int id = Integer.parseInt(arPath[2]);
                        SubTask savedTask = taskManager.getSubTask(id);
                        if (savedTask == null) {
                            sendStatus(httpExchange, 404);
                        } else {
                            taskManager.updateSubTask(newTask);
                        }
                    } else {
                        taskManager.addSubTask(newTask);
                        sendStatus(httpExchange, 201);
                    }
                } catch (ManagerAddTaskException e) {
                    sendHasInteractions(httpExchange);
                } catch (Exception e) {
                    sendError(httpExchange, 500);
                }
            } else if (method.contains("DELETE")) {
                String[] arPath = httpExchange.getRequestURI().getPath().split("/");
                if (arPath.length > 2) { // path: /tasks/{id}/
                    try {
                        int id = Integer.parseInt(arPath[2]);
                        taskManager.deleteSubTask(id);
                        sendStatus(httpExchange, 200);
                    } catch (Exception e) {
                        sendNotFound(httpExchange);
                    }
                }
            } else if (method.contains("GET")) {
                String[] arPath = httpExchange.getRequestURI().getPath().split("/");
                if (arPath.length > 2) { // path: /tasks/{id}/
                    try {
                        int id = Integer.parseInt(arPath[2]);
                        SubTask task = taskManager.getSubTask(id);
                        sendJson(httpExchange, task);
                    } catch (Exception e) {
                        sendNotFound(httpExchange);
                    }
                } else { // path: /tasks/
                    List<SubTask> tasks = taskManager.getSubTasks();
                    if (tasks != null) {
                        sendJson(httpExchange, tasks);
                    } else {
                        sendNotFound(httpExchange);
                    }
                }
            }
            sendStatus(httpExchange, 404);
        }
    }

    public static class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod().trim();

            if (method.contains("POST")) { // path: /tasks/
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
            } else if (method.contains("DELETE")) {
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
            } else if (method.contains("GET")) {
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
            sendStatus(httpExchange, 404);
        }
    }

    public static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                List<Task> hist = taskManager.getHistoryManager().getHistory();
                sendJson(httpExchange, hist);
            } catch (Exception e) {
                sendStatus(httpExchange, 200);
            }
        }
    }

    public static class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                sendJson(httpExchange, taskManager.getPrioritizedTasks());
            } catch (Exception e) {
                sendStatus(httpExchange, 200);
            }
        }
    }
}
