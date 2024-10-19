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

    public int addTask(Task task) {
        tasks.put(task.id, task);
        return task.id;
    }

    public int addSubTask(SubTask subtask) {
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.epycId);
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
        updateEpicStatus(subtask.epycId);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id))
            tasks.remove(id);
    }

    public void deleteTasks(int id) {
        for (Task t : getTasks())
            deleteTask(t.id);
    }

    public void deleteSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epycId = subTasks.get(id).epycId;
            subTasks.remove(id);
            updateEpicStatus(epycId);
        }
    }

    public void deleteSubTasks(int id) {
        for (SubTask t : getSubTasks())
            deleteSubTask(t.id);
    }

    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> sub = new ArrayList<>();

        for (SubTask t : subTasks.values())
            if (t.epycId == epicId)
                sub.add(t);

        return sub;
    }

    public Task getTask(int id) {
        if (!tasks.containsKey(id))
            return null;
        return tasks.get(id);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public SubTask getSubTask(int id) {
        if (!subTasks.containsKey(id))
            return null;
        return subTasks.get(id);
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Epic getEpic(int id) {
        if (!epics.containsKey(id))
            return null;
        return epics.get(id);
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteEpic(int id) {
        // удаляем все задачи из эпика, если они есть
        for (SubTask s : getEpicSubTasks(id))
            deleteSubTask(s.id);

        // удаляем сам эпик
        if (epics.containsKey(id))
            epics.remove(id);
    }

    public void deleteEpics() {
        for (Epic e : getEpics())
            if (e != null)
                deleteEpic(e.id);
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = getEpic(epicId);

        if (epic == null)
            return;

        epic.updateStatus(getEpicSubTasks(epicId));
        updateEpic(epic);
    }

    @Override
    public String toString() {
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

    /* Далее методы для лёгкого использования всей системы в Main */
    public Task createTask(String name, String text) {
        Task t = new Task();
        t.setName(name);
        t.setText(text);
        addTask(t);
        return t;
    }

    public Epic createEpic(String name, String text, String[][] subTaskInfo) {
        Epic epic = new Epic();
        epic.setName(name).setText(text);

        List<Integer> sbtasks_ids = new ArrayList<>();
        for (String[] pair : subTaskInfo) {
            SubTask st = new SubTask(epic.id);
            st.setName(pair[0]);
            st.setText(pair[1]);
            int st_id = addSubTask(st);
            sbtasks_ids.add(st_id);
        }
        epic.setSubTasksIds(sbtasks_ids);

        addEpic(epic);

        return epic;
    }
}
