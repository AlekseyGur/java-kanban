package ru.alexgur.kanban.tests;

import java.util.ArrayList;
import java.util.List;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskManager;

public class Tests {
    public void start() {
        TaskManager tm = new TaskManager();

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
        System.out.println(tm.printAll());

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
            SubTask st = new SubTask(epic.id);
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