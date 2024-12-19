package test.ru.alexgur.kanban.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.exceptions.ManageLoadException;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.FileBackedTaskManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new FileBackedTaskManager();
    }

    // Методы родителя
    @Test
    public void testAddNewTask() {
        addNewTask();
    }

    @Test
    public void testAddNewSubTask() {
        addNewSubTask();
    }

    @Test
    public void testAddNewEpic() {
        addNewEpic();
    }

    @Test
    public void testUpdateTask() {
        updateTask();
    }

    @Test
    public void testUpdateSubTask() {
        updateTask();
    }

    @Test
    public void testUpdateEpic() {
        updateTask();
    }

    @Test
    public void testDeleteTask() {
        deleteTask();
    }

    @Test
    public void testDeleteTasks() {
        deleteTasks();
    }

    @Test
    public void testDeleteSubTask() {
        deleteSubTask();
    }

    @Test
    public void testDeleteSubTasks() {
        deleteSubTasks();
    }

    @Test
    public void testDeleteEpic() {
        deleteEpic();
    }

    @Test
    public void testDeleteEpics() {
        deleteEpics();
    }

    @Test
    public void testGetEpicSubTasks() {
        getEpicSubTasks();
    }

    @Test
    public void testGetTask() {
        getTask();
    }

    @Test
    public void testGetTasks() {
        getTasks();
    }

    @Test
    public void testGetSubTask() {
        getSubTask();
    }

    @Test
    public void testGetSubTasks() {
        getSubTasks();
    }

    @Test
    public void testGetEpic() {
        getEpic();
    }

    @Test
    public void testGetEpics() {
        getEpics();
    }

    // Уникальные методы этого класса
    @Test
    public void shouldSaveAndLoadFromEmptyCSVFile() {
        // сохранение и загрузку пустого файла;
        try {
            File tempFile = writeCsvWithoutContent();
            taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (ManageLoadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(taskManager.getTasks().size(), 0);
        Assertions.assertEquals(taskManager.getSubTasks().size(), 0);
        Assertions.assertEquals(taskManager.getEpics().size(), 0);
    }

    @Test
    public void shouldSaveFewTasksToCSVFile() {
        // сохранение нескольких задач;

        try {
            taskManager.setFile(File.createTempFile("kanban_to_save", ".csv"));
        } catch (IOException e) {
            //
        }

        Task task1 = new Task(1);
        Task task2 = new Task(2);
        Epic epic1 = new Epic(3);
        SubTask subTask1 = new SubTask(4);

        task1.setName("task Name 1").setText("task Text 1");
        task2.setName("task Name 2").setText("task Text 2");
        epic1.setName("epic Name 1").setText("epic Text 1");
        subTask1.setName("subTask Name 1").setText("subTask Text 1");

        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);

        List<Integer> sbTasksIds = new ArrayList<>();
        sbTasksIds.add(subTask1.id);
        epic1.setSubTasksIds(sbTasksIds);
        subTask1.setEpicId(epic1.id);

        taskManager.addTask(task2);

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(taskManager.getFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("id") || line.isEmpty()) {
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            //
        }

        Assertions.assertEquals("1;TASK;task Name 1;task Text 1;NEW;;0;;", lines.get(0));
        Assertions.assertEquals("2;TASK;task Name 2;task Text 2;NEW;;0;;", lines.get(1));
        Assertions.assertEquals("4;SUBTASK;subTask Name 1;subTask Text 1;NEW;;0;3;", lines.get(2));
        Assertions.assertEquals("3;EPIC;epic Name 1;epic Text 1;NEW;;0;;", lines.get(3));

        Assertions.assertEquals(2, taskManager.getTasks().size());
        Assertions.assertEquals(1, taskManager.getSubTasks().size());
        Assertions.assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    public void shouldLoadFewTasksFromCSVFile() {
        // загрузку нескольких задач.
        try {
            File tempFile = writeCsvWithContent();
            taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (ManageLoadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(1, taskManager.getSubTasks().size());
        Assertions.assertEquals(1, taskManager.getEpics().size());

        Assertions.assertEquals(List.of(3), (List<Integer>) taskManager.getEpics().get(0).getSubTasksIds());
        Assertions.assertEquals(2, taskManager.getSubTasks().get(0).getEpicId());
        Assertions.assertEquals(1, taskManager.getTasks().get(0).getId());
    }

    private static File writeCsvWithoutContent() throws IOException {
        File tempFile = File.createTempFile("kanban_empty_file", ".csv");
        Writer fileWriter = new FileWriter(tempFile);
        fileWriter.write("");
        fileWriter.close();

        return tempFile;
    }

    private static File writeCsvWithContent() throws IOException {
        String EOL = "\n";
        String SPL = ";";

        File tempFile = File.createTempFile("kanban_to_load", ".csv");
        Writer fileWriter = new FileWriter(tempFile);

        fileWriter.write(
                "id" + SPL + "type" + SPL + "name" + SPL + "description" + SPL + "status" + SPL + "start" + SPL
                        + "duration" + SPL + "epic" + EOL);
        fileWriter.write(
                "1" + SPL + "TASK" + SPL + "Task1" + SPL + "Description task1" + SPL + "NEW" + SPL
                        + "2024-12-19 04:12:41" + SPL + "20" + SPL + SPL + EOL);
        fileWriter.write(
                "2" + SPL + "EPIC" + SPL + "Epic2" + SPL + "Description epic2" + SPL + "DONE" + SPL
                        + "2024-12-20 07:23:17" + SPL + "30" + SPL + SPL + EOL);
        fileWriter.write(
                "3" + SPL + "SUBTASK" + SPL + "Sub Task2" + SPL + "Description sub task3" + SPL + "DONE" + SPL
                        + "2024-12-21 10:27:58" + SPL + "40" + SPL + "2" + EOL);

        fileWriter.close();

        return tempFile;
    }
}