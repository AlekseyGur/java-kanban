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
            SubTask sub = subTasks.get(id);
            int epicId = sub.epicId;
            subTasks.remove(id);

            if (epics.containsKey(epicId)) {
                Epic epic = getEpic(epicId);
                epic.deleteSubTask(id);
                updateEpicStatus(epicId);
            }
        }
    }

    private void clearEpic(int id) {
        List<Integer> epic_subs = getEpic(id).getSubTasksIds();
        if (epics.containsKey(id)) {
            epics.remove(id);
        }
        for (Integer sid : epic_subs) {
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
        for (Task t : getTasks()) {
            clearTask(t.id);
        }
    }

    public void deleteSubTask(int id) {
        clearSubTask(id);
    }

    public void deleteSubTasks() {
        for (SubTask t : getSubTasks()) {
            clearSubTask(t.id);
        }
    }

    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> sub = new ArrayList<>();

        for (SubTask t : subTasks.values()) {
            if (t.epicId == epicId) {
                sub.add(t);
            }
        }

        return sub;
    }

    public Task getTask(int id) {
        if (!tasks.containsKey(id)) {
            return null;
        }
        return tasks.get(id);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public SubTask getSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return null;
        }
        return subTasks.get(id);
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Epic getEpic(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        return epics.get(id);
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteEpic(int id) {
        clearEpic(id);
    }

    public void deleteEpics() {
        for (Epic e : getEpics()) {
            if (e != null) {
                clearEpic(e.id);
            }
        }
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = getEpic(epicId);

        if (epic == null) {
            return;
        }

        updateEpicStatus(epic, getEpicSubTasks(epicId));
        updateEpic(epic);
    }

    public void updateEpicStatus(Epic epic, List<SubTask> subTasks) {
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
