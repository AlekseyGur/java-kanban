package ru.alexgur.kanban.service;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

import ru.alexgur.kanban.exceptions.ManagerAddTaskException;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HistoryManager history;
    private TreeSet<Task> treeOfTasksAndSubTasks = new TreeSet<>(Task::compareToStartTimeAsc);

    public void setHistoryManager(HistoryManager manager) {
        history = manager;
    }

    public HistoryManager getHistoryManager() {
        return history;
    }

    @Override
    public int addTask(Task task) {
        if (isTaskCrossesSavedTasks(task)) {
            throw new ManagerAddTaskException("Задача пересекается с другими по времени!");
        }
        tasks.put(task.id, task);
        addTreeOfTasksAndSubTasks(task);
        return task.id;
    }

    @Override
    public int addSubTask(SubTask subtask) {
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.getEpicId());
        addTreeOfTasksAndSubTasks(subtask);
        return subtask.id;
    }

    @Override
    public int addEpic(Epic epic) {
        epics.put(epic.id, epic);
        updateEpicStatus(epic.id);
        return epic.id;
    }

    @Override
    public void updateTask(Task task) {
        updateTreeOfTasksAndSubTasks(task);
        tasks.put(task.id, task);
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        updateTreeOfTasksAndSubTasks(subtask);
        subTasks.put(subtask.id, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    @Override
    public void deleteTask(int id) {
        removeTreeOfTasksAndSubTasks(getTaskImpl(id));
        clearTask(id);
    }

    @Override
    public void deleteTasks() {
        deleteTypeFromTreeOfTasksAndSubTasks(TaskType.TASK);
        tasks.clear();
    }

    @Override
    public void deleteSubTask(int id) {
        removeTreeOfTasksAndSubTasks(getSubTaskImpl(id));
        clearSubTask(id);
    }

    @Override
    public void deleteSubTasks() {
        epics.values().forEach(epic -> {
            epic.deleteSubTasks();
            updateEpicStatus(epic.getId());
        });
        deleteTypeFromTreeOfTasksAndSubTasks(TaskType.SUBTASK);
        subTasks.clear();
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        return getEpicSubTasksImpl(epicId);
    }

    @Override
    public Task getTask(int id) {
        Task task = getTaskImpl(id);
        addToHistoryImpl(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        addToHistoryImpl(subTask);
        return subTask;
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = getEpicImpl(id);
        addToHistoryImpl(epic);
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return getEpicsImpl();
    }

    @Override
    public void deleteEpic(int id) {
        clearEpic(id);
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();

        deleteTypeFromTreeOfTasksAndSubTasks(TaskType.SUBTASK);
    }

    public void updateEpicsSubTasksIds() {
        epics.values().forEach(epic -> {
            epic.setSubTasksIds(
                    getEpicSubTasksImpl(epic.getId()).stream()
                            .map(x -> x.getId()).toList());
            setEpicDurationStartEndTime(epic);
        });
    }

    public void updateEpicsDurationStartEndTime() {
        epics.values().forEach(x -> setEpicDurationStartEndTime(x));
    }

    public List<Task> getPrioritizedTasks() {
        return Stream.concat(getTasks().stream(), getSubTasks().stream())
                .filter(x -> x.getStartTime() == null)
                .sorted(Task::compareToStartTimeAsc)
                .toList();
    }

    private List<Epic> getEpicsImpl() {
        return new ArrayList<>(epics.values());
    }

    private List<SubTask> getEpicSubTasksImpl(int epicId) {
        List<SubTask> epicSubTasks = subTasks.values().stream()
                .filter(x -> x.getEpicId() == epicId)
                .toList();

        return epicSubTasks;
    }

    private <T extends Task> void addToHistoryImpl(T task) {
        if (history != null) {
            history.add(task);
        }
    }

    private void setEpicDurationStartEndTime(Epic epic) {
        if (epic == null) {
            return;
        }

        List<SubTask> subtasks = getEpicSubTasksImpl(epic.getId());
        Duration duration = subtasks.stream()
                .map(x -> x.getDuration())
                .reduce(Duration.ZERO, (x, y) -> x.plus(y));

        LocalDateTime startTime = subtasks.stream()
                .map(x -> x.getStartTime())
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (duration.isZero()) {
            epic.setDuration(duration);
        }
        if (startTime != null) {
            epic.setStartTime(startTime);
            epic.setEndTime(epic.getStartTime().plus(duration));
        }
    }

    private Epic getEpicImpl(int id) {
        return epics.get(id);
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicImpl(epicId);

        if (epic == null) {
            return;
        }

        updateEpicStatus(epic, getEpicSubTasksImpl(epicId));
        updateEpic(epic);
        setEpicDurationStartEndTime(epic);
    }

    private void updateEpicStatus(Epic epic, List<SubTask> subTasks) {
        boolean isNew = subTasks.stream().allMatch(
                task -> task.getStatus() == Status.NEW);

        boolean isDone = subTasks.stream().allMatch(
                task -> task.getStatus() == Status.DONE);

        Status newStatus;
        if (isNew) {
            // все подзадачи имеют статус NEW или их нет
            newStatus = Status.NEW;
        } else if (isDone) {
            // все подзадачи имеют DONE
            newStatus = Status.DONE;
        } else {
            newStatus = Status.IN_PROGRESS;
        }

        epic.setStatus(newStatus);
    }

    private void clearTask(int id) {
        if (tasks.containsKey(id)) {
            Task removedTask = tasks.remove(id);
            removeTreeOfTasksAndSubTasks(removedTask);
        }
    }

    private void clearSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask sub = subTasks.remove(id);
            removeTreeOfTasksAndSubTasks(sub);
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
        epic.getSubTasksIds().forEach(sid -> clearSubTask(sid));
    }

    private Task getTaskImpl(int id) {
        Task task = tasks.get(id);
        return task;
    }

    private SubTask getSubTaskImpl(int id) {
        SubTask subTask = subTasks.get(id);
        return subTask;
    }

    private void addTreeOfTasksAndSubTasks(Task task) {
        if (!task.getDuration().isZero() &&
                task.getStartTime() != null &&
                task.getEndTime() != null) {
            treeOfTasksAndSubTasks.add(task);
        }
    }

    private <T extends Task> void removeTreeOfTasksAndSubTasks(T task) {
        treeOfTasksAndSubTasks.remove(task);
    }

    private <T extends Task> void updateTreeOfTasksAndSubTasks(T task) {
        Task el = (task.getType() == TaskType.TASK ? tasks : subTasks).get(task.id);
        removeTreeOfTasksAndSubTasks(el);
        addTreeOfTasksAndSubTasks(task);
    }

    private List<Task> deleteTypeFromTreeOfTasksAndSubTasks(TaskType type) {
        return treeOfTasksAndSubTasks.stream().filter(x -> x.getType() != type).toList();
    }

    public <T extends Task> boolean isTaskCrossesSavedTasks(T a) {
        final LocalDateTime end = a.getEndTime();
        final LocalDateTime start = a.getStartTime();

        if (end == null || start == null) {
            return false;
        }

        return treeOfTasksAndSubTasks.stream()
                .anyMatch(x -> {
                    LocalDateTime thisStart = x.getStartTime();
                    LocalDateTime thisEnd = x.getEndTime();

                    if (thisEnd.isAfter(start) && thisEnd.isBefore(end)) {
                        return true;
                    }

                    if ((thisStart.isAfter(start) || thisStart.isEqual(start))
                            && !thisStart.isAfter(end)) {
                        return true;
                    }
                    return false;
                });
    }
}
