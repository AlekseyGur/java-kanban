package test.ru.alexgur.kanban.service;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class InMemoryTaskManagerTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    // создайте тест, в котором проверяется неизменность задачи (по всем полям) при
    // добавлении задачи в менеджер
    @Test
    public void shouldPersistTaskAfterSave() {
        Task task = new Task();

        int taskId = tm.addTask(task);

        Assertions.assertEquals(task, tm.getTask(taskId));
    }

    // проверьте, что InMemoryTaskManager действительно добавляет задачи разного
    // типа и может найти их по id;
    @Test
    public void shouldSaveAllTypesToInMemoryTaskManager() {
        Task task = new Task();
        SubTask subTask = new SubTask();
        Epic epic = new Epic();

        int taskId = tm.addTask(task);
        int subTaskId = tm.addSubTask(subTask);
        int epicId = tm.addEpic(epic);

        Assertions.assertEquals(task, tm.getTask(taskId));
        Assertions.assertEquals(subTask, tm.getSubTask(subTaskId));
        Assertions.assertEquals(epic, tm.getEpic(epicId));
    }

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют
    // внутри менеджера; ?????? Это вообще как?

}