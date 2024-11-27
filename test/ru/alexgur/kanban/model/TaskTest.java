package test.ru.alexgur.kanban.model;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.Managers;
import ru.alexgur.kanban.service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class TaskTest {
    private static TaskManager tm;
    private static HistoryManager hm;

    @BeforeAll
    public static void createTaskManagerAndHistoryManagerVarsSetHistoryManager() {
        tm = Managers.getDefault();
        hm = Managers.getDefaultHistory();
        tm.setHistoryManager(hm);
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

    // проверьте, что экземпляры класса Task равны друг другу;
    @Test
    public void shouldBeEqualTwoTasks() {
        Task task1 = new Task();
        Task task2 = new Task();

        task1.setName("Название").setText("Описание");
        task2.setName("Название").setText("Описание");

        Assertions.assertEquals(task1, task2);
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

    // проверьте, что наследники класса Task равны друг другу;
    @Test
    public void shouldBeEqualTwoSubTasks() {
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setName("Название SubTask").setText("Описание SubTask");
        subTask2.setName("Название SubTask").setText("Описание SubTask");

        Assertions.assertEquals(subTask1, subTask2);
    }
 
}