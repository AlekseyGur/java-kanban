public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();

        // Создаём первую эпик задачу
        Epic epic1 = tm.addEpic("Переезд");

        // Добавляем подзадачи для эпика 1
        SubTask task1 = tm.addSubTask(epic1, "Собрать коробки");
        SubTask task2 = tm.addSubTask(epic1, "Упаковать кошку");
        tm.addSubTask(epic1, "Сказать слова прощания");

        // Создаём вторую эпик задачу
        Epic epic2 = tm.addEpic("Важный эпик");

        // Добавляем подзадачу для второго эпика
        SubTask task4 = tm.addSubTask(epic2, "Одна подзадача");

        // Изменяем статусы созданных объектов и печатаем их
        tm.setSubTaskStatus(task1.id, Status.IN_PROGRESS);
        tm.setSubTaskStatus(task2.id, Status.DONE);
        tm.setSubTaskStatus(task4.id, Status.DONE);
        tm.printAll();

        // Теты
        System.out.println("Автотесты:");

        // Проверяем - статус подзадачи изменился
        Status status;
        status = tm.getSubTaskById(task1.id).getStatus();
        if (status == Status.IN_PROGRESS)
            System.out.println("✅ - Статус подзадачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка проверки изменения статуса - 1");

        status = tm.getSubTaskById(task2.id).getStatus();
        if (status == Status.DONE)
            System.out.println("✅ - Статус подзадачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка проверки изменения статуса - 2");

        status = tm.getSubTaskById(task4.id).getStatus();
        if (status == Status.DONE)
            System.out.println("✅ - Статус подзадачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка проверки изменения статуса - 3");

        // Проверяем - статус эпика изменился
        status = tm.getEpicById(epic2.id).getStatus();
        if (status == Status.DONE)
            System.out.println("✅ - Статус эпичной задачи успешно изменился на: " + status);
        else
            System.out.println("❌ - Ошибка изменения статуса эпичной задачи");

        // Удалим одну задачу из эпика
        int s = tm.getAllSubTasksByEpic(epic1).size();
        tm.deleteSubTaskById(task2.id);
        if (tm.getAllSubTasksByEpic(epic1).size() == --s)
            System.out.println("✅ - В эпике осталось " + s + " задач. Все верно.");
        else
            System.out.println("❌ - Ошибка удаления задачи из эпика");

        // Удаляем один эпик
        tm.deleteEpic(epic2);
        if (tm.getEpicById(epic2.id) == null)
            System.out.println("✅ - Эпик задачи успешно удаляются");
        else
            System.out.println("❌ - Ошибка удаления эпик задачи");
    }
}
