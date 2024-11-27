package test.ru.alexgur.kanban.service;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class HistoryManagerTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую
    // версию задачи и её данных.
    @Test
    public void shouldPersistTaskAfterAddMoreTasks() {
        Task task = new Task();
        int taskId = tm.addTask(task);

        tm.addTask(new Task());
        tm.addTask(new Task());
        tm.addTask(new Task());
        tm.addTask(new Task());

        Assertions.assertEquals(task, tm.getTask(taskId));
    }
}