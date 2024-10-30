package ru.alexgur.kanban.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskManager;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.InMemoryHistoryManager;
import ru.alexgur.kanban.service.Managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class Tests {

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
        subTask1.setName("Название SubTask").setText("Описание SubTask");

        SubTask subTask2 = new SubTask();
        subTask2.setName("Название SubTask").setText("Описание SubTask");

        Assertions.assertEquals(subTask1, subTask2);
    }

    // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    @Test
    public void shouldNotBePossibleToAddEpicAsSubTask() {
        Epic epic = new Epic();

        List<Integer> sbtasks_ids = new ArrayList<>();

        sbtasks_ids.add(epic.id);

        epic.setSubTasksIds(sbtasks_ids);

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
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);

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
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
        Task task = new Task();
        int taskId = tm.addTask(task);
        Assertions.assertEquals(task, tm.getTask(taskId));
    }

    // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую
    // версию задачи и её данных.
    @Test
    public void shouldPersistTaskAfterAddMoreTasks() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
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
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
        tm.getHistoryManager().clear();

        Task task = new Task();
        int taskId = tm.addTask(task);
        tm.getTask(taskId);

        List<Task> hist = tm.getHistoryManager().getHistory();
        Assertions.assertEquals(taskId, hist.get(0).id);
        Assertions.assertEquals(1, hist.size());
    }

    // проверка добавления в историю и чтения из неё ровно MAX элементов
    @Test
    public void shouldSaveAndReturnOneRecordsFromHistory() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
        tm.getHistoryManager().clear();

        int max_hist_size = InMemoryHistoryManager.HISTORY_MAX_SIZE;

        for (int i = 0; i < max_hist_size; i++) {
            Task task = new Task();
            tm.addTask(task);
            tm.getTask(task.id);
        }

        List<Task> hist = tm.getHistoryManager().getHistory();

        Assertions.assertTrue(max_hist_size == hist.size());
    }

    // проверка добавления в историю и чтения из неё ровно MAX+1 элементов
    @Test
    public void shouldSaveAndReturnMaxPlusOneRecordFromHistory() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
        tm.getHistoryManager().clear();

        int max_hist_size = InMemoryHistoryManager.HISTORY_MAX_SIZE;

        for (int i = 0; i < max_hist_size + 1; i++) {
            Task task = new Task();
            tm.addTask(task);
            tm.getTask(task.id);
        }

        List<Task> hist = tm.getHistoryManager().getHistory();
        Assertions.assertTrue(max_hist_size == hist.size());
    }

    @Test
    public void shouldCreateEpicAndAddSubTaskAndSetProperies() {
        Epic epic = new Epic();
        epic.setName("Название Epic").setText("Описание Epic");

        List<Integer> sbtasks_ids = new ArrayList<>();

        SubTask subTask1 = new SubTask();
        subTask1.setName("Название SubTask").setText("Описание SubTask");
        sbtasks_ids.add(subTask1.id);

        SubTask subTask2 = new SubTask();
        subTask2.setName("Название SubTask").setText("Описание SubTask");
        sbtasks_ids.add(subTask2.id);

        epic.setSubTasksIds(sbtasks_ids);

        Assertions.assertEquals("Название Epic", epic.getName());
        Assertions.assertEquals("Описание Epic", epic.getText());
        Assertions.assertEquals(2, epic.getSubTasksIds().size());
    }

    @Test
    void addToHistory() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);

        Task task = new Task();
        tm.addTask(task);
        tm.getTask(task.id);
        
        final List<Task> history = hm.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    } 

    @Test
    void addNewTask() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);

        Task task = new Task();
        task.setName("Название").setText("Описание");

        final int taskId = tm.addTask(task);

        final Task savedTask = tm.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = tm.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    } 

    @Test
    public void shouldWorkAllTaskTogatherLikeInProduction() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);

        // Создаём первую эпик задачу
        String[][] subs1 = {
                { "Первая подзадача", "Собрать коробки" },
                { "Втоаря подзадача", "Упаковать кошку" },
                { "Третья подзадача", "Сказать слова прощания" }
        };
        Epic epic1 = createEpic(
                tm,
                "Переезд",
                "Перва эпик задача",
                subs1);

        // Создаём вторую эпик задачу
        String[][] subs2 = {
                { "Небольшое дело", "закончить программу" }
        };
        Epic epic2 = createEpic(
                tm,
                "Вторая эпик задача",
                "Описание задачи",
                subs2);

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
        int st_size = st.size();
        tm.deleteSubTask(st.get(0).id);
        Assertions.assertTrue(tm.getEpicSubTasks(epic1.id).size() == --st_size);

        // Удаляем один эпик
        tm.deleteEpic(epic2.id);
        Assertions.assertTrue(tm.getEpic(epic2.id) == null);
    }

    public void startManual() {

        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);

        // Создаём первую эпик задачу
        String[][] subs1 = {
                { "Первая подзадача", "Собрать коробки" },
                { "Втоаря подзадача", "Упаковать кошку" },
                { "Третья подзадача", "Сказать слова прощания" }
        };
        Epic epic1 = createEpic(
                tm,
                "Переезд",
                "Перва эпик задача",
                subs1);

        // Создаём вторую эпик задачу
        String[][] subs2 = {
                { "Небольшое дело", "закончить программу" }
        };
        Epic epic2 = createEpic(
                tm,
                "Вторая эпик задача",
                "Описание задачи",
                subs2);

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
        int st_size = st.size();
        tm.deleteSubTask(st.get(0).id);
        if (tm.getEpicSubTasks(epic1.id).size() == --st_size)
            System.out
                    .println("✅ - Успешно удалена задача из эпика. В эпике осталось " + st_size + " задач. Все верно.");
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

    /* Далее методы для лёгкого использования всей системы в Main */
    public Task createTask(TaskManager tm, String name, String text) {
        Task t = new Task();
        t.setName(name);
        t.setText(text);
        tm.addTask(t);
        return t;
    }

    public Epic createEpic(TaskManager tm, String name, String text, String[][] subTaskInfo) {
        Epic epic = new Epic();
        epic.setName(name).setText(text);

        List<Integer> sbtasks_ids = new ArrayList<>();
        for (String[] pair : subTaskInfo) {
            SubTask st = new SubTask();
            st.setEpicId(epic.id);
            st.setName(pair[0]);
            st.setText(pair[1]);
            int st_id = tm.addSubTask(st);
            sbtasks_ids.add(st_id);
        }
        epic.setSubTasksIds(sbtasks_ids);

        tm.addEpic(epic);

        return epic;
    }
}