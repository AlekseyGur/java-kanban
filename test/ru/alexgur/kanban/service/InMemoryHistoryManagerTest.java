package test.ru.alexgur.kanban.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.InMemoryHistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class InMemoryHistoryManagerTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    // проверка добавления в историю и чтения из неё
    @Test
    public void shouldSaveAndReturnOneRecordFromHistory() {
        clearHistory();
        Task task = new Task();

        int taskId = tm.addTask(task);
        tm.getTask(taskId);
        List<Task> hist = tm.getHistoryManager().getHistory();

        Assertions.assertEquals(taskId, hist.get(0).id);
        Assertions.assertEquals(1, hist.size());
    }

    // проверка добавления в историю и чтения из неё ровно 10 элементов
    @Test
    public void shouldSaveAndReturnTenRecordsFromHistory() {
        clearHistory();
        int maxHistSize = 10;

        for (int i = 0; i < maxHistSize; i++) {
            Task task = new Task();
            tm.addTask(task);
            tm.getTask(task.id);
        }
        List<Task> hist = tm.getHistoryManager().getHistory();

        Assertions.assertTrue(maxHistSize == hist.size());
    }

    // проверка добавления в историю и чтения из неё ровно 10+1 элементов
    @Test
    public void shouldSaveAndReturnTenPlusOneRecordFromHistory() {
        clearHistory();
        int maxHistSize = 10;

        for (int i = 0; i < maxHistSize + 1; i++) {
            Task task = new Task();
            tm.addTask(task);
            tm.getTask(task.id);
        }

        List<Task> hist = tm.getHistoryManager().getHistory();
        Assertions.assertTrue(maxHistSize + 1 == hist.size());
    }

    @Test
    void addToHistory() {
        clearHistory();
        Task task = new Task();

        tm.addTask(task);
        tm.getTask(task.id);
        final List<Task> history = hm.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    private void clearHistory() {
        InMemoryHistoryManager thm = (InMemoryHistoryManager) tm.getHistoryManager();
        thm.clear();
    }
}