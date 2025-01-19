package ru.alexgur.kanban.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import ru.alexgur.kanban.service.handlers.EpicHandler;
import ru.alexgur.kanban.service.handlers.HistoryHandler;
import ru.alexgur.kanban.service.handlers.PrioritizedHandler;
import ru.alexgur.kanban.service.handlers.SubTaskHandler;
import ru.alexgur.kanban.service.handlers.TaskHandler;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final String TASKS_PATH = "/tasks";
    private static final String SUBTASKS_PATH = "/subtasks";
    private static final String EPICS_PATH = "/epics";
    private static final String HISTORY_PATH = "/history";
    private static final String PRIORITIZED_PATH = "/prioritized";
    public static final String IP = "127.0.0.1"; // "localhost";
    public static final int PORT = 8080;
    public HttpServer server;
    public TaskManager taskManager;
    public static Gson gson;

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start(taskManager);
    }

    public void start(TaskManager taskManager) {
        this.taskManager = taskManager;
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(IP, PORT), 0);
            server.createContext(TASKS_PATH, new TaskHandler(taskManager));
            server.createContext(SUBTASKS_PATH, new SubTaskHandler(taskManager));
            server.createContext(EPICS_PATH, new EpicHandler(taskManager));
            server.createContext(HISTORY_PATH, new HistoryHandler(taskManager));
            server.createContext(PRIORITIZED_PATH, new PrioritizedHandler(taskManager));
            server.setExecutor(null);

            server.start();
            System.out.println("HttpServer started at port " + PORT);
        } catch (IOException e) {
            System.out.println("HttpServer error: " + e);
        }
    }

    public void stop() {
        server.stop(0);
        System.out.println("HttpServer stopped at port " + PORT);
    }

}
