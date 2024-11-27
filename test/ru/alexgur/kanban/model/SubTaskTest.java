package test.ru.alexgur.kanban.model;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class SubTaskTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    @Test
    public void shouldCreateSubTaskAndSetProperies() {
        SubTask subTask = new SubTask();

        subTask.setName("Название");
        subTask.setText("Описание");

        Assertions.assertEquals("Название", subTask.getName());
        Assertions.assertEquals("Описание", subTask.getText());
    }

    // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    public void shouldNotBePossibleToSetSubTaskAsItsOwnEpic() {
        SubTask subTask = new SubTask();

        subTask.setEpicId(subTask.id);

        Assertions.assertNotEquals(subTask.id, subTask.getEpicId());
    }
}