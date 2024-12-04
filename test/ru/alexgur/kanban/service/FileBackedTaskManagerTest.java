package test.ru.alexgur.kanban.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.Task;
import ru.alexgur.kanban.service.HistoryManager;
import ru.alexgur.kanban.service.ManageLoadException;
import ru.alexgur.kanban.service.FileBackedTaskManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.jupiter.api.Assertions;

public class FileBackedTaskManagerTest {
    public static FileBackedTaskManager tm;
    public static HistoryManager hm;

    @Test
    public void shouldSaveAndLoadFromEmptyCSVFile() {
        // сохранение и загрузку пустого файла;
        try {
            File tempFile = writeCsvWithoutContent();
            tm = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (ManageLoadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(tm.getTasks().size(), 0);
        Assertions.assertEquals(tm.getSubTasks().size(), 0);
        Assertions.assertEquals(tm.getEpics().size(), 0);
    }

    @Test
    public void shouldSaveFewTasksToCSVFile() {
        // сохранение нескольких задач;
        tm = new FileBackedTaskManager();

        try {
            tm.setFile(File.createTempFile("kanban_to_save", ".csv"));
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

        tm.addTask(task1);
        tm.addEpic(epic1);
        tm.addSubTask(subTask1);

        List<Integer> sbTasksIds = new ArrayList<>();
        sbTasksIds.add(subTask1.id);
        epic1.setSubTasksIds(sbTasksIds);
        subTask1.setEpicId(epic1.id);

        tm.addTask(task2);

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(tm.getFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("id") || line.isEmpty()) {
                    continue;
                }
                System.out.println(line);
                lines.add(line);
            }
        } catch (IOException e) {
            //
        }

        Assertions.assertEquals("1;TASK;task Name 1;NEW;task Text 1;;", lines.get(0));
        Assertions.assertEquals("2;TASK;task Name 2;NEW;task Text 2;;", lines.get(1));
        Assertions.assertEquals("4;SUBTASK;subTask Name 1;NEW;subTask Text 1;3;", lines.get(2));
        Assertions.assertEquals("3;EPIC;epic Name 1;NEW;epic Text 1;;", lines.get(3));

        Assertions.assertEquals(2, tm.getTasks().size());
        Assertions.assertEquals(1, tm.getSubTasks().size());
        Assertions.assertEquals(1, tm.getEpics().size());
    }

    @Test
    public void shouldLoadFewTasksFromCSVFile() {
        // загрузку нескольких задач.
        try {
            File tempFile = writeCsvWithContent();
            tm = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (ManageLoadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(1, tm.getTasks().size());
        Assertions.assertEquals(1, tm.getSubTasks().size());
        Assertions.assertEquals(1, tm.getEpics().size());

        Assertions.assertEquals(List.of(3), (List<Integer>) tm.getEpics().get(0).getSubTasksIds());
        Assertions.assertEquals(2, tm.getSubTasks().get(0).getEpicId());
        Assertions.assertEquals(1, tm.getTasks().get(0).getId());
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
                "id" + SPL + "type" + SPL + "name" + SPL + "status" + SPL + "description" + SPL + "epic" + EOL);
        fileWriter.write(
                "1" + SPL + "TASK" + SPL + "Task1" + SPL + "NEW" + SPL + "Description task1" + SPL + SPL + EOL);
        fileWriter.write(
                "2" + SPL + "EPIC" + SPL + "Epic2" + SPL + "DONE" + SPL + "Description epic2" + SPL + SPL + EOL);
        fileWriter.write("3" + SPL + "SUBTASK" + SPL + "Sub Task2" + SPL + "DONE" + SPL + "Description sub task3"
                + SPL + "2" + EOL);

        fileWriter.close();

        return tempFile;
    }
}