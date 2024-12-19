package test.ru.alexgur.kanban.model;

import org.junit.jupiter.api.Test;

import ru.alexgur.kanban.model.SubTask;
import org.junit.jupiter.api.Assertions;

public class SubTaskTest {
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

    // проверьте, что наследники класса SubTask равны друг другу;
    @Test
    public void shouldBeEqualTwoSubTasks() {
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();

        subTask1.setName("Название SubTask").setText("Описание SubTask");
        subTask2.setName("Название SubTask").setText("Описание SubTask");

        Assertions.assertEquals(subTask1, subTask2);
    }
}