package test.ru.alexgur.kanban.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class EpicTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    @Test
    public void shouldCreateEpicAndAddSubTaskAndSetProperies() {
        Epic epic = new Epic();
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();
        List<Integer> sbTasksIds = new ArrayList<>();

        epic.setName("Название Epic").setText("Описание Epic");
        subTask1.setName("Название SubTask").setText("Описание SubTask");
        subTask2.setName("Название SubTask").setText("Описание SubTask");
        sbTasksIds.add(subTask1.id);
        sbTasksIds.add(subTask2.id);
        epic.setSubTasksIds(sbTasksIds);

        Assertions.assertEquals("Название Epic", epic.getName());
        Assertions.assertEquals("Описание Epic", epic.getText());
        Assertions.assertEquals(2, epic.getSubTasksIds().size());
    }

    // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    @Test
    public void shouldNotBePossibleToAddEpicAsSubTask() {
        Epic epic = new Epic();
        List<Integer> sbTasksIds = new ArrayList<>();

        sbTasksIds.add(epic.id);
        epic.setSubTasksIds(sbTasksIds);

        Assertions.assertEquals(0, epic.getSubTasksIds().size());
    }
}