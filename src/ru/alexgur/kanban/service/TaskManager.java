package ru.alexgur.kanban.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;

public class TaskManager {
    // Ключ хеша совпадает с Task.id, используется для быстрого поиска
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    private void clearTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        }
    }

    private void clearSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask sub = subTasks.remove(id);
            int epicId = sub.epicId;

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

    public int addTask(Task task) {
        tasks.put(task.id, task);
        return task.id;
    }

    public int addSubTask(SubTask subtask) {
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.epicId);
        return subtask.id;
    }

    public int addEpic(Epic epic) {
        epics.put(epic.id, epic);
        return epic.id;
    }

    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }

    public void updateSubTask(SubTask subtask) {
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.epicId);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    public void deleteTask(int id) {
        clearTask(id);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTask(int id) {
        clearSubTask(id);
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTasks = new ArrayList<>();

        for (SubTask subtask : subTasks.values()) {
            if (subtask.epicId == epicId) {
                epicSubTasks.add(subtask);
            }
        }

        return epicSubTasks;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteEpic(int id) {
        clearEpic(id);
    }

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

    public String printAll() {
        String res = "";
        for (Epic e : epics.values()) {
            res += "Эпик: " + e + "\n";

            List<SubTask> st = getEpicSubTasks(e.id);
            if (st.isEmpty()) {
                res += "Не содержит задач." + "\n";
            } else {
                res += "Задачи:" + "\n";
                for (SubTask s : getEpicSubTasks(e.id)) {
                    res += " - " + s + "\n";
                }
            }
            res += "\n";
        }
        return res;
    }

}
