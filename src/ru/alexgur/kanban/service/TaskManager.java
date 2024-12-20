package ru.alexgur.kanban.service;

import java.util.List;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;

public interface TaskManager {

    HistoryManager getHistoryManager();

    void setHistoryManager(HistoryManager manager);

    int addTask(Task task);

    int addSubTask(SubTask subtask);

    int addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteTasks();

    void deleteSubTask(int id);

    void deleteSubTasks();

    List<SubTask> getEpicSubTasks(int epicId);

    Task getTask(int id);

    List<Task> getTasks();

    SubTask getSubTask(int id);

    List<SubTask> getSubTasks();

    Epic getEpic(int id);

    List<Epic> getEpics();

    void deleteEpic(int id);

    void deleteEpics();

    List<Task> getPrioritizedTasks();
}