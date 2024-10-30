package ru.alexgur.kanban.service;

import java.util.ArrayList;
import java.util.List;

import ru.alexgur.kanban.model.Task;

public class InMemoryHistoryManager implements HistoryManager {

    public static final int HISTORY_MAX_SIZE = 10;
    private ArrayList<Task> history = new ArrayList<>();

    private void addImpl(Task task) {
        if (task != null) {
            history.add(task);
            if (history.size() > HISTORY_MAX_SIZE) {
                history.remove(0);
            }
        }
    }

    private List<Task> getHistoryImpl() {
        return history;
    }

    private void clearImpl() {
        history.clear();
    }

    @Override
    public void clear() {
        clearImpl();
    }

    @Override
    public void add(Task task) {
        addImpl(task);
    }

    @Override
    public List<Task> getHistory() {
        return getHistoryImpl();
    }

}
