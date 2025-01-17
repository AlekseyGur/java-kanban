package ru.alexgur.kanban.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import ru.alexgur.kanban.service.HttpTaskManager.EpicHandler;
import ru.alexgur.kanban.service.HttpTaskManager.HistoryHandler;
import ru.alexgur.kanban.service.HttpTaskManager.PrioritizedHandler;
import ru.alexgur.kanban.service.HttpTaskManager.SubTaskHandler;
import ru.alexgur.kanban.service.HttpTaskManager.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer extends BaseHttpHandler {
    private static final String TASKS_PATH = "/tasks";
    private static final String SUBTASKS_PATH = "/subtasks";
    private static final String EPICS_PATH = "/epics";
    private static final String HISTORY_PATH = "/history";
    private static final String PRIORITIZED_PATH = "/prioritized";
    public static final String IP = "127.0.0.1"; // "localhost";
    public static final int PORT = 8070;
    public static HttpServer server;
    public static TaskManager taskManager;
    public static Gson gson = HttpTaskServer.getGson();

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        prepareServer(taskManager);
        start();
    }

    public HttpTaskServer(TaskManager taskManager) {
        prepareServer(taskManager);
        start();
    }

    private static void prepareServer(TaskManager taskManager) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        HttpTaskServer.taskManager = taskManager;
    }

    public static void start() {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(IP, PORT), 0);
            server.createContext(TASKS_PATH, new TaskHandler());
            server.createContext(SUBTASKS_PATH, new SubTaskHandler());
            server.createContext(EPICS_PATH, new EpicHandler());
            server.createContext(HISTORY_PATH, new HistoryHandler());
            server.createContext(PRIORITIZED_PATH, new PrioritizedHandler());
            server.setExecutor(null);

            server.start();
            System.out.println("HttpServer started at port " + PORT);
        } catch (IOException e) {
            System.out.println("HttpServer error: " + e);
        }
    }

    public static void stop() {
        server.stop(0);
    }

}
