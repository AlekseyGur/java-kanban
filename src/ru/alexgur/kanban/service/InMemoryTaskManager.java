package ru.alexgur.kanban.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;

public class InMemoryTaskManager implements TaskManager {
    // Ключ хеша совпадает с Task.id, используется для быстрого поиска
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private HistoryManager history;

    public void setHistoryManager(HistoryManager manager) {
        history = manager;
    }

    public HistoryManager getHistoryManager() {
        return history;
    }

    private void clearTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    private void clearSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask sub = subTasks.remove(id);
            int epicId = sub.getEpicId();

            if (epics.containsKey(epicId)) {
                Epic epic = getEpic(epicId);
                epic.deleteSubTask(id);
                updateEpicStatus(epicId);
            }
        }
    }

    private void clearEpic(int id) {
        Epic epic = epics.remove(id);
        for (Integer sid : epic.getSubTasksIds()) {
            clearSubTask(sid);
        }
    }

    @Override
    public int addTask(Task task) {
        tasks.put(task.id, task);
        return task.id;
    }

    @Override
    public int addSubTask(SubTask subtask) {
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.getEpicId());
        return subtask.id;
    }

    @Override
    public int addEpic(Epic epic) {
        epics.put(epic.id, epic);
        return epic.id;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    @Override
    public void deleteTask(int id) {
        clearTask(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubTask(int id) {
        clearSubTask(id);
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTasks = new ArrayList<>();

        for (SubTask subtask : subTasks.values()) {
            if (subtask.getEpicId() == epicId) {
                epicSubTasks.add(subtask);
            }
        }

        return epicSubTasks;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        history.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        history.add(subTask);
        return subTask;
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        history.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpic(int id) {
        clearEpic(id);
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpic(epicId);

        if (epic == null) {
            return;
        }

        updateEpicStatus(epic, getEpicSubTasks(epicId));
        updateEpic(epic);
    }

    private void updateEpicStatus(Epic epic, List<SubTask> subTasks) {
        boolean isNew = true;
        boolean isDone = true;

        for (SubTask t : subTasks) {
            if (t.getStatus() != Status.NEW) {
                isNew = false;
            }
            if (t.getStatus() != Status.DONE) {
                isDone = false;
            }
        }

        Status newStatus;
        if (isNew) {
            newStatus = Status.NEW;
        } // все подзадачи имеют статус NEW или их нет
        else if (isDone) {
            newStatus = Status.DONE;
        } // все подзадачи имеют DONE
        else {
            newStatus = Status.IN_PROGRESS;
        }

        epic.setStatus(newStatus);
    }

}
