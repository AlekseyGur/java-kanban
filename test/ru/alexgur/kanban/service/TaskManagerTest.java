package test.ru.alexgur.kanban.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.TaskManager;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected HistoryManager historyManager;

    // int addTask(Task task);
    @Test
    public void addNewTask() {
        Task task = new Task();

        task.setName("Название").setText("Описание");
        final int taskSizeInit = taskManager.getTasks().size();
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(taskSizeInit + 1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(tasks.size() - 1), "Задачи не совпадают.");
    }

    // int addSubTask(SubTask subtask);
    @Test
    public void addNewSubTask() {
        SubTask task = new SubTask();

        task.setName("Название").setText("Описание");
        final int taskSizeInit = taskManager.getSubTasks().size();
        final int taskId = taskManager.addSubTask(task);
        final SubTask savedSubTask = taskManager.getSubTask(taskId);
        final List<SubTask> tasks = taskManager.getSubTasks();

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(task, savedSubTask, "Задачи не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(taskSizeInit + 1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(tasks.size() - 1), "Задачи не совпадают.");
    }

    // int addEpic(Epic epic);
    @Test
    public void addNewEpic() {
        Epic task = new Epic();

        task.setName("Название").setText("Описание");
        final int taskSizeInit = taskManager.getEpics().size();
        final int taskId = taskManager.addEpic(task);
        final Epic savedEpic = taskManager.getEpic(taskId);
        final List<Epic> tasks = taskManager.getEpics();

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(task, savedEpic, "Задачи не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(taskSizeInit + 1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(tasks.size() - 1), "Задачи не совпадают.");
    }

    // void updateTask(Task task);
    @Test
    public void updateTask() {
        Task task = new Task();

        int taskId = taskManager.addTask(task);

        task.setName("Название")
                .setText("Описание");

        Task savedTask = taskManager.getTask(taskId);

        assertEquals(task, savedTask, "Задачи Task не совпадают.");
    }

    // void updateSubTask(SubTask subtask);
    @Test
    public void updateSubTask() {
        SubTask task = new SubTask();

        int taskId = taskManager.addSubTask(task);

        task.setName("Название")
                .setText("Описание");

        Task savedTask = taskManager.getSubTask(taskId);

        assertEquals(task, savedTask, "Задачи SubTask не совпадают.");
    }

    // void updateEpic(Epic epic);
    @Test
    public void updateEpic() {
        Epic task = new Epic();

        int taskId = taskManager.addEpic(task);

        task.setName("Название")
                .setText("Описание");

        Epic savedEpic = taskManager.getEpic(taskId);

        assertEquals(task, savedEpic, "Задачи Epic не совпадают.");
    }

    // void deleteTask(int id);
    @Test
    public void deleteTask() {
        Task task = new Task();

        task.setName("Название")
                .setText("Описание");

        int taskId = taskManager.addTask(task);

        taskManager.deleteTask(taskId);
        Task saved = taskManager.getTask(taskId);

        assertEquals(saved, null, "Задачи Task не удаляются");
    }

    // void deleteTasks();
    @Test
    public void deleteTasks() {
        Task task1 = new Task();
        Task task2 = new Task();

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        int size1 = taskManager.getTasks().size();
        taskManager.deleteTasks();
        int size2 = taskManager.getTasks().size();

        assertEquals(size1, 2, "Список Task задач не дополняется");
        assertEquals(size2, 0, "Список Task задач не очищается");
    }

    // void deleteSubTask(int id);
    @Test
    public void deleteSubTask() {
        SubTask task = new SubTask();

        task.setName("Название")
                .setText("Описание");

        int taskId = taskManager.addSubTask(task);
        taskManager.deleteSubTask(taskId);
        SubTask saved = taskManager.getSubTask(taskId);

        assertEquals(saved, null, "Задачи SubTask не удаляются");
    }

    // void deleteSubTasks();
    @Test
    public void deleteSubTasks() {
        SubTask task1 = new SubTask();
        SubTask task2 = new SubTask();

        taskManager.addSubTask(task1);
        taskManager.addSubTask(task2);

        int size1 = taskManager.getSubTasks().size();
        taskManager.deleteSubTasks();
        int size2 = taskManager.getSubTasks().size();

        assertEquals(size1, 2, "Список подзадач не дополняется");
        assertEquals(size2, 0, "Список подзадач не очищается");
    }

    // void deleteEpic(int id);
    @Test
    public void deleteEpic() {
        Epic task = new Epic();

        task.setName("Название")
                .setText("Описание");

        int taskId = taskManager.addEpic(task);
        taskManager.deleteEpic(taskId);
        Epic saved = taskManager.getEpic(taskId);

        assertEquals(saved, null, "Задачи Epic не удаляются");
    }

    // void deleteEpics();
    @Test
    public void deleteEpics() {
        Epic task1 = new Epic();
        Epic task2 = new Epic();

        taskManager.addEpic(task1);
        taskManager.addEpic(task2);

        int size1 = taskManager.getEpics().size();
        taskManager.deleteEpics();
        int size2 = taskManager.getEpics().size();

        assertEquals(size1, 2, "Список Epic задач не дополняется");
        assertEquals(size2, 0, "Список Epic задач не очищается");
    }

    // List<SubTask> getEpicSubTasks(int epicId);
    @Test
    public void getEpicSubTasks() {
        Epic epic = new Epic();
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        epic.setSubTasksIds(List.of(subTask1.id, subTask2.id));
        
        subTask1.setEpicId(epic.id);
        subTask2.setEpicId(epic.id);

        int subTaskId1 = taskManager.addSubTask(subTask1);
        int subTaskId2 = taskManager.addSubTask(subTask2);
        int epicId = taskManager.addEpic(epic);

        List<Integer> saved = taskManager.getEpic(epicId).getSubTasksIds();

        Assertions.assertEquals(subTaskId1, saved.get(0));
        Assertions.assertEquals(subTaskId2, saved.get(1));
    }

    // Task getTask(int id);
    @Test
    public void getTask() {
        Task task1 = new Task();

        int taskId = taskManager.addTask(task1);

        int size = taskManager.getTasks().size();
        Task savedTask1 = taskManager.getTask(taskId);

        assertEquals(size, 1, "Список Task задач не дополняется");
        assertEquals(task1, savedTask1, "Задача Task1 не совпадает с сохранённой");
    }

    // List<Task> getTasks();
    @Test
    public void getTasks() {
        Task task1 = new Task();
        Task task2 = new Task();

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        int size = taskManager.getTasks().size();
        Task savedTask1 = taskManager.getTasks().get(0);
        Task savedTask2 = taskManager.getTasks().get(1);

        assertEquals(size, 2, "Список Task задач не дополняется");
        assertEquals(task1, savedTask1, "Задача Task1 не совпадает с сохранённой");
        assertEquals(task2, savedTask2, "Задача Task2 не совпадает с сохранённой");
    }

    // SubTask getSubTask(int id);
    @Test
    public void getSubTask() {
        SubTask task1 = new SubTask();

        int taskId = taskManager.addSubTask(task1);

        int size = taskManager.getSubTasks().size();
        SubTask savedSubTask1 = taskManager.getSubTask(taskId);

        assertEquals(size, 1, "Список SubTask задач не дополняется");
        assertEquals(task1, savedSubTask1, "Задача SubTask1 не совпадает с сохранённой");
    }

    // List<SubTask> getSubTasks();
    @Test
    public void getSubTasks() {
        SubTask task1 = new SubTask();
        SubTask task2 = new SubTask();

        taskManager.addSubTask(task1);
        taskManager.addSubTask(task2);

        int size = taskManager.getSubTasks().size();
        SubTask savedSubTask1 = taskManager.getSubTasks().get(0);
        SubTask savedSubTask2 = taskManager.getSubTasks().get(1);

        assertEquals(size, 2, "Список SubTask задач не дополняется");
        assertEquals(task1, savedSubTask1, "Задача SubTask1 не совпадает с сохранённой");
        assertEquals(task2, savedSubTask2, "Задача SubTask2 не совпадает с сохранённой");
    }

    // Epic getEpic(int id);
    @Test
    public void getEpic() {
        Epic task1 = new Epic();

        int taskId = taskManager.addEpic(task1);

        int size = taskManager.getEpics().size();
        Epic savedEpic1 = taskManager.getEpic(taskId);

        assertEquals(size, 1, "Список Epic задач не дополняется");
        assertEquals(task1, savedEpic1, "Задача Epic1 не совпадает с сохранённой");
    }

    // List<Epic> getEpics();
    @Test
    public void getEpics() {
        Epic task1 = new Epic();
        Epic task2 = new Epic();

        taskManager.addEpic(task1);
        taskManager.addEpic(task2);

        int size = taskManager.getEpics().size();
        Epic savedEpic1 = taskManager.getEpics().get(0);
        Epic savedEpic2 = taskManager.getEpics().get(1);

        assertEquals(size, 2, "Список Epic задач не дополняется");
        assertEquals(task1, savedEpic1, "Задача Epic1 не совпадает с сохранённой");
        assertEquals(task2, savedEpic2, "Задача Epic2 не совпадает с сохранённой");
    }
}
