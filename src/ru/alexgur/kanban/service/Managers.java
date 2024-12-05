package ru.alexgur.kanban.service;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
        // return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}