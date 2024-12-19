package test.ru.alexgur.kanban.model;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.Task;
import org.junit.jupiter.api.Assertions;

public class TaskTest {
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
}