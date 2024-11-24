package test.ru.alexgur.kanban;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import main.ru.alexgur.kanban.model.Epic;
import main.ru.alexgur.kanban.model.SubTask;
import main.ru.alexgur.kanban.model.Task;
import main.ru.alexgur.kanban.service.HistoryManager;
import main.ru.alexgur.kanban.service.Managers;
import main.ru.alexgur.kanban.service.Status;
import main.ru.alexgur.kanban.service.TaskManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class Tests {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
    }

    // проверьте, что экземпляры класса Task равны друг другу;
    @Test
    public void shouldBeEqualTwoTasks() {
        Task task1 = new Task();
        Task task2 = new Task();

        task1.setName("Название").setText("Описание");
        task2.setName("Название").setText("Описание");

        Assertions.assertEquals(task1, task2);
    }

    // проверьте, что наследники класса Task равны друг другу;
    @Test
    public void shouldBeEqualTwoSubTasks() {
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setName("Название SubTask").setText("Описание SubTask");
        subTask2.setName("Название SubTask").setText("Описание SubTask");

        Assertions.assertEquals(subTask1, subTask2);
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

    // проверьте, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    public void shouldNotBePossibleToSetSubTaskAsItsOwnEpic() {
        SubTask subTask = new SubTask();

        subTask.setEpicId(subTask.id);

        Assertions.assertNotEquals(subTask.id, subTask.getEpicId());
    }

    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и
    // готовые к работе экземпляры менеджеров;
    @Test
    public void shouldReturnInitAndRedyManagers() {
        Assertions.assertTrue(Managers.getDefault() instanceof TaskManager);
        Assertions.assertTrue(Managers.getDefaultHistory() instanceof HistoryManager);
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

    // создайте тест, в котором проверяется неизменность задачи (по всем полям) при
    // добавлении задачи в менеджер
    @Test
    public void shouldPersistTaskAfterSave() {
        Task task = new Task();

        int taskId = tm.addTask(task);

        Assertions.assertEquals(task, tm.getTask(taskId));
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

    @Test
    public void shouldIncrementCountByOneAfterTaskCreate() {
        Task task1 = new Task();
        Task task2 = new Task();

        Assertions.assertEquals(task1.id + 1, task2.id);
    }

    @Test
    public void shouldCreateTaskAndSetProperies() {
        Task task = new Task();

        task.setName("Название");
        task.setText("Описание");

        Assertions.assertEquals("Название", task.getName());
        Assertions.assertEquals("Описание", task.getText());
    }

    @Test
    public void shouldCreateSubTaskAndSetProperies() {
        SubTask subTask = new SubTask();

        subTask.setName("Название");
        subTask.setText("Описание");

        Assertions.assertEquals("Название", subTask.getName());
        Assertions.assertEquals("Описание", subTask.getText());
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

    @Test
    void addNewTask() {
        Task task = new Task();

        task.setName("Название").setText("Описание");
        final int taskSizeInit = tm.getTasks().size();
        final int taskId = tm.addTask(task);
        final Task savedTask = tm.getTask(taskId);
        final List<Task> tasks = tm.getTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(taskSizeInit + 1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(tasks.size() - 1), "Задачи не совпадают.");
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

    public void startManual() {

        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);

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

        // Печатаем всё
        printAllTasks(tm);

        // Теты
        System.out.println("Автотесты:");

        // Проверяем - статус подзадачи изменился
        Status status;
        status = tm.getSubTask(st1.id).getStatus();
        if (status == Status.IN_PROGRESS)
            System.out.println("✅ - Статус подзадачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка проверки изменения статуса - 1");

        status = tm.getSubTask(st2.id).getStatus();
        if (status == Status.DONE)
            System.out.println("✅ - Статус подзадачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка проверки изменения статуса - 2");

        status = tm.getSubTask(st3.id).getStatus();
        if (status == Status.DONE)
            System.out.println("✅ - Статус подзадачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка проверки изменения статуса - 3");

        // Проверяем - статус эпика 1 изменился
        status = tm.getEpic(epic1.id).getStatus();
        if (status == Status.IN_PROGRESS)
            System.out.println("✅ - Статус эпичной задачи 1 успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка изменения статуса эпичной задачи");

        // Проверяем - статус эпика 2 изменился
        status = tm.getEpic(epic2.id).getStatus();
        if (status == Status.DONE)
            System.out.println("✅ - Статус эпичной задачи 2 успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка изменения статуса эпичной задачи");

        // Удалим одну задачу из эпика
        List<SubTask> st = tm.getEpicSubTasks(epic1.id);
        int stSize = st.size();
        tm.deleteSubTask(st.get(0).id);
        if (tm.getEpicSubTasks(epic1.id).size() == --stSize)
            System.out
                    .println("✅ - Успешно удалена задача из эпика. В эпике осталось " + stSize + " задач. Все верно.");
        else
            System.out.println("❌ - Ошибка удаления задачи из эпика");

        // Удаляем один эпик
        tm.deleteEpic(epic2.id);
        if (tm.getEpic(epic2.id) == null)
            System.out.println("✅ - Эпик задачи успешно удаляются");
        else
            System.out.println("❌ - Ошибка удаления эпик задачи");
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubTasks(epic.id)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }

    private void clearHistory() {
        tm.getHistoryManager().clear();
    }
}