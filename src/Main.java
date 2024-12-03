import java.util.List;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        Main ex = new Main();
        ex.startManual();
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
}
