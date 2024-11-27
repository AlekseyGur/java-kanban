package test.ru.alexgur.kanban.service;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class ManagersTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и
    // готовые к работе экземпляры менеджеров;
    @Test
    public void shouldReturnInitAndRedyManagers() {
        Assertions.assertTrue(Managers.getDefault() instanceof TaskManager);
        Assertions.assertTrue(Managers.getDefaultHistory() instanceof HistoryManager);
    }
}