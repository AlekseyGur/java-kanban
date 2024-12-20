package test.ru.alexgur.kanban;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.InMemoryHistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class MainTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    // Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
    // Запросите созданные задачи несколько раз в разном порядке.
    // После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
    @Test
    void shouldCreateTwoTasksAndOneEpicWithThreeSubtasksAndOneEptyEpic() {
        clearHistory();
        List<Task> hist = tm.getHistoryManager().getHistory();

        Task task1 = new Task();
        tm.addTask(task1);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании задачи история просмотров не должна изменяться.");

        Task task2 = new Task();
        tm.addTask(task2);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании задачи история просмотров не должна изменяться.");

        Epic epic1 = new Epic();
        tm.addEpic(epic1);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании эпик задачи история просмотров не должна изменяться.");

        Epic epic2 = new Epic();
        tm.addEpic(epic2);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании эпик задачи история просмотров не должна изменяться.");

        SubTask subTask1 = new SubTask();
        tm.addSubTask(subTask1);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании подзадачи история просмотров не должна изменяться.");

        SubTask subTask2 = new SubTask();
        tm.addSubTask(subTask2);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании подзадачи история просмотров не должна изменяться.");

        SubTask subTask3 = new SubTask();
        tm.addSubTask(subTask3);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При создании подзадачи история просмотров не должна изменяться.");

        task1.setName("Название1").setText("Описание1");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров задачи история просмотров не должна изменяться.");

        task2.setName("Название2").setText("Описание2");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров задачи история просмотров не должна изменяться.");

        epic1.setName("Название Epic 1").setText("Описание Epic 1");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров эпик задачи история просмотров не должна изменяться.");

        epic2.setName("Название Epic 2").setText("Описание Epic 2");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров эпик задачи история просмотров не должна изменяться.");

        subTask1.setName("Название SubTask 1").setText("Описание SubTask 1");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров подзадачи история просмотров не должна изменяться.");

        subTask2.setName("Название SubTask 2").setText("Описание SubTask 2");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров подзадачи история просмотров не должна изменяться.");

        subTask3.setName("Название SubTask 3").setText("Описание SubTask 3");
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При изменении параметров подзадачи история просмотров не должна изменяться.");

        List<Integer> sbTasksIds = new ArrayList<>();
        sbTasksIds.add(subTask1.id);
        sbTasksIds.add(subTask2.id);
        sbTasksIds.add(subTask3.id);
        epic1.setSubTasksIds(sbTasksIds);
        subTask1.setEpicId(epic1.id);
        subTask2.setEpicId(epic1.id);
        subTask3.setEpicId(epic1.id);
        hist = tm.getHistoryManager().getHistory();
        assertEquals(hist.size(), 0, "При добавлении подзадачи к эпику история просмотров не должна изменяться.");

        tm.getTask(task1.id);
        hist = tm.getHistoryManager().getHistory();
        assertTrue(hist.get(0).equals(task1));
        assertTrue(hist.size() == 1);

        tm.getTask(task2.id);
        hist = tm.getHistoryManager().getHistory();
        assertTrue(hist.get(0).equals(task2));
        assertTrue(hist.get(1).equals(task1));
        assertTrue(hist.size() == 2);

        clearHistory();

        tm.getTask(task2.id);
        hist = tm.getHistoryManager().getHistory();
        assertTrue(hist.get(0).equals(task2));
        assertTrue(hist.size() == 1);

        tm.getTask(task1.id);
        hist = tm.getHistoryManager().getHistory();
        assertTrue(hist.get(0).equals(task1));
        assertTrue(hist.get(1).equals(task2));
        assertTrue(hist.size() == 2);

        clearHistory();

        tm.getTask(task1.id);
        tm.getTask(task2.id);
        tm.getTask(task1.id);
        hist = tm.getHistoryManager().getHistory();
        assertTrue(hist.get(0).equals(task1));
        assertTrue(hist.get(1).equals(task2));
        assertTrue(hist.size() == 2);

        clearHistory();

        tm.getTask(task1.id);
        tm.getTask(task2.id);
        tm.getTask(task1.id);
        tm.getEpic(epic2.id);
        tm.getSubTask(subTask2.id);
        tm.getSubTask(subTask1.id);
        tm.getEpic(epic1.id);
        tm.getTask(task1.id);
        tm.getEpic(epic2.id);
        hist = tm.getHistoryManager().getHistory();

        assertTrue(hist.get(0).equals(epic2));
        assertTrue(hist.get(1).equals(task1));
        assertTrue(hist.get(2).equals(epic1));
        assertTrue(hist.get(3).equals(subTask1));
        assertTrue(hist.get(4).equals(subTask2));
        assertTrue(hist.get(5).equals(task2));
        assertTrue(hist.size() == 6);
    }

    @Test
    public void shouldWorkAllTaskTogatherLikeInProduction() {

        // Создаём первую эпик задачу
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();
        SubTask subTask3 = new SubTask();
        Epic epic1 = new Epic();

        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        tm.addSubTask(subTask3);
        tm.addEpic(epic1);

        subTask1.setName("Первая подзадача").setText("Собрать коробки");
        subTask1.setName("Втоаря подзадача").setText("Упаковать кошку");
        subTask1.setName("Третья подзадача").setText("Сказать слова прощания");
        epic1.setName("Переезд").setText("Перва эпик задача");

        epic1.setSubTasksIds(List.of(subTask1.id, subTask2.id, subTask3.id));
        subTask1.setEpicId(epic1.id);
        subTask2.setEpicId(epic1.id);
        subTask3.setEpicId(epic1.id);

        // Создаём вторую эпик задачу
        SubTask subTask4 = new SubTask();
        Epic epic2 = new Epic();

        tm.addSubTask(subTask4);
        tm.addEpic(epic2);

        subTask4.setName("Небольшое дело").setText("закончить программу");
        epic2.setName("Вторая эпик задача").setText("Описание задачи");

        epic2.setSubTasksIds(List.of(subTask4.id));
        subTask4.setEpicId(epic2.id);

        // Изменяем статусы созданных объектов и печатаем их
        SubTask st1 = tm.getEpicSubTasks(epic1.id).get(0);
        SubTask st2 = tm.getEpicSubTasks(epic1.id).get(1);
        SubTask st3 = tm.getEpicSubTasks(epic2.id).get(0);
        st1.setStatus(Status.IN_PROGRESS);
        st2.setStatus(Status.DONE);
        st3.setStatus(Status.DONE);
        tm.updateSubTask(st1);
        tm.updateSubTask(st2);
        tm.updateSubTask(st3);

        // Проверяем - статус подзадачи изменился
        Status status;
        status = tm.getSubTask(st1.id).getStatus();
        Assertions.assertTrue(status == Status.IN_PROGRESS);

        status = tm.getSubTask(st2.id).getStatus();
        Assertions.assertTrue(status == Status.DONE);

        status = tm.getSubTask(st3.id).getStatus();
        Assertions.assertTrue(status == Status.DONE);

        status = tm.getEpic(epic1.id).getStatus();
        Assertions.assertTrue(status == Status.IN_PROGRESS);

        // Проверяем - статус эпика 2 изменился
        status = tm.getEpic(epic2.id).getStatus();
        Assertions.assertTrue(status == Status.DONE);

        // Удалим одну задачу из эпика
        List<SubTask> st = tm.getEpicSubTasks(epic1.id);
        int stSize = st.size();
        tm.deleteSubTask(st.get(0).id);
        Assertions.assertTrue(tm.getEpicSubTasks(epic1.id).size() == --stSize);

        // Удаляем один эпик
        tm.deleteEpic(epic2.id);
        Assertions.assertTrue(tm.getEpic(epic2.id) == null);
    }

    private void clearHistory() {
        InMemoryHistoryManager thm = (InMemoryHistoryManager) tm.getHistoryManager();
        thm.clear();
    }
}