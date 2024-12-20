package test.ru.alexgur.kanban.service;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.exceptions.ManagerAddTaskException;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.InMemoryTaskManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
    }

    // Методы родителя
    @Test
    public void testAddNewTask() {
        addNewTask();
    }

    @Test
    public void testAddNewSubTask() {
        addNewSubTask();
    }

    @Test
    public void testAddNewEpic() {
        addNewEpic();
    }

    @Test
    public void testUpdateTask() {
        updateTask();
    }

    @Test
    public void testUpdateSubTask() {
        updateTask();
    }

    @Test
    public void testUpdateEpic() {
        updateTask();
    }

    @Test
    public void testDeleteTask() {
        deleteTask();
    }

    @Test
    public void testDeleteTasks() {
        deleteTasks();
    }

    @Test
    public void testDeleteSubTask() {
        deleteSubTask();
    }

    @Test
    public void testDeleteSubTasks() {
        deleteSubTasks();
    }

    @Test
    public void testDeleteEpic() {
        deleteEpic();
    }

    @Test
    public void testDeleteEpics() {
        deleteEpics();
    }

    @Test
    public void testGetEpicSubTasks() {
        getEpicSubTasks();
    }

    @Test
    public void testGetTask() {
        getTask();
    }

    @Test
    public void testGetTasks() {
        getTasks();
    }

    @Test
    public void testGetSubTask() {
        getSubTask();
    }

    @Test
    public void testGetSubTasks() {
        getSubTasks();
    }

    @Test
    public void testGetEpic() {
        getEpic();
    }

    @Test
    public void testGetEpics() {
        getEpics();
    }

    @Test
    public void testGetPrioritizedTasks() {
        getPrioritizedTasks();
    }

    @Test
    public void testSetEpicDurationStartEndTime() {
        setEpicDurationStartEndTime();
    }

    // Уникальные методы этого класса

    // тест, в котором проверяется неизменность задачи (по всем полям) при
    // добавлении задачи в менеджер
    @Test
    public void shouldPersistTaskAfterSave() {
        Task task = new Task();

        int taskId = taskManager.addTask(task);

        Assertions.assertEquals(task, taskManager.getTask(taskId));
    }

    // проверьте, что Manager действительно добавляет задачи разного
    // типа и может найти их по id;
    @Test
    public void shouldSaveAllTaskTypes() {
        Task task = new Task();
        SubTask subTask = new SubTask();
        Epic epic = new Epic();

        int taskId = taskManager.addTask(task);
        int subTaskId = taskManager.addSubTask(subTask);
        int epicId = taskManager.addEpic(epic);

        Assertions.assertEquals(task, taskManager.getTask(taskId));
        Assertions.assertEquals(subTask, taskManager.getSubTask(subTaskId));
        Assertions.assertEquals(epic, taskManager.getEpic(epicId));
    }

    // Для расчёта статуса Epic. Граничные условия:
    @Test
    public void shouldSetEpicNewIfAllSubtasksNew() {
        // a. Все подзадачи со статусом NEW.
        Epic epic = new Epic();
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.NEW);
        epic.setSubTasksIds(List.of(subTask1.id, subTask2.id));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        int epicId = taskManager.addEpic(epic);

        Assertions.assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void shouldSetEpicDoneIfAllSubtasksDone() {
        // b. Все подзадачи со статусом DONE.
        Epic epic = new Epic();
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        epic.setSubTasksIds(List.of(subTask1.id, subTask2.id));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask1.setEpicId(epic.id);
        subTask2.setEpicId(epic.id);
        int epicId = taskManager.addEpic(epic);

        Assertions.assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void shouldSetEpicInProgressIfAllSubtasksNewAndDone() {
        // c. Подзадачи со статусами NEW и DONE.
        Epic epic = new Epic();
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        epic.setSubTasksIds(List.of(subTask1.id, subTask2.id));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask1.setEpicId(epic.id);
        subTask2.setEpicId(epic.id);
        int epicId = taskManager.addEpic(epic);

        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void shouldSetEpicInProgressIfAllSubtasksInProgress() {
        // d. Подзадачи со статусом IN_PROGRESS.
        Epic epic = new Epic();
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        epic.setSubTasksIds(List.of(subTask1.id, subTask2.id));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask1.setEpicId(epic.id);
        subTask2.setEpicId(epic.id);
        int epicId = taskManager.addEpic(epic);

        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus());
    }

    @Test
    public void shouldThrowExceptionIfCrossInterval() {
        // c. Добавить тест на корректность расчёта пересечения интервалов.
        Task task1 = new Task();
        SubTask task2 = new SubTask();
        Task task3 = new Task();
        Task task4 = new Task();
        SubTask task5 = new SubTask();
        Task task6 = new Task();

        task1.setStartTime(LocalDateTime.parse("2024-12-10 09:18:32", Task.dateTimeFormatter));
        task2.setStartTime(LocalDateTime.parse("2024-12-10 09:18:32", Task.dateTimeFormatter));
        task3.setStartTime(LocalDateTime.parse("2024-12-10 09:18:41", Task.dateTimeFormatter));
        task4.setStartTime(LocalDateTime.parse("2024-12-10 09:19:42", Task.dateTimeFormatter));
        task5.setStartTime(LocalDateTime.parse("2024-12-10 02:02:42", Task.dateTimeFormatter));
        task6.setStartTime(LocalDateTime.parse("2024-12-10 02:02:42", Task.dateTimeFormatter));

        task1.setDuration(Duration.ofSeconds(10));
        task2.setDuration(Duration.ofSeconds(10));
        task3.setDuration(Duration.ofSeconds(10));
        task4.setDuration(Duration.ofSeconds(10));
        task5.setDuration(Duration.ofSeconds(10));
        task6.setDuration(Duration.ofSeconds(10));

        Assertions.assertDoesNotThrow(() -> taskManager.addTask(task1));
        Assertions.assertThrows(ManagerAddTaskException.class, () -> taskManager.addSubTask(task2));
        Assertions.assertThrows(ManagerAddTaskException.class, () -> taskManager.addTask(task3));
        Assertions.assertDoesNotThrow(() -> taskManager.addTask(task4));

        Assertions.assertThrows(ManagerAddTaskException.class, () -> {
            task1.setStartTime(LocalDateTime.parse("2024-12-10 09:19:42", Task.dateTimeFormatter));
            taskManager.updateTask(task1);
        });

        Assertions.assertThrows(ManagerAddTaskException.class, () -> {
            task1.setStartTime(LocalDateTime.parse("2024-12-10 13:56:12", Task.dateTimeFormatter));
            taskManager.updateTask(task1);
        });

    }
}