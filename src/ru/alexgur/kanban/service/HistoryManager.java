package ru.alexgur.kanban.service;

import java.util.List;

import ru.alexgur.kanban.model.Task;

public interface HistoryManager {

    void add(Task task);

    void clear();

    void remove(int id);

    List<Task> getHistory();

}