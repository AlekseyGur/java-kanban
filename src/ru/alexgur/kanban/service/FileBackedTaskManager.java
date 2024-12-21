package ru.alexgur.kanban.service;

import java.util.List;

import ru.alexgur.kanban.exceptions.ManageLoadException;
import ru.alexgur.kanban.exceptions.ManagerSaveException;
import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileToSave;
    private DateTimeFormatter dateTimeFormatter = Task.dateTimeFormatter;
    private String eol = "\n";
    private String csvSplitter = ";";

    @Override
    public int addTask(Task task) {
        int newId = super.addTask(task);
        save();
        return newId;
    }

    @Override
    public int addEpic(Epic epic) {
        int newId = super.addEpic(epic);
        save();
        return newId;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        int newId = super.addSubTask(subTask);
        save();
        return newId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    public File getFile() {
        return fileToSave;
    }

    public void setFile(File fileToSave) {
        this.fileToSave = fileToSave;
    }

    public String getEol() {
        return eol;
    }

    public void setEol(String eol) {
        this.eol = eol;
    }

    public String getCsvSplitter() {
        return csvSplitter;
    }

    public void setCsvSplitter(String csvSplitter) {
        this.csvSplitter = csvSplitter;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("id") || line.isEmpty()) {
                    continue;
                }

                Task task = (Task) taskManager.fromString(line);

                if (task.getType() == TaskType.TASK) {
                    taskManager.addTask(task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    taskManager.addSubTask((SubTask) task);
                } else if (task.getType() == TaskType.EPIC) {
                    taskManager.addEpic((Epic) task);
                }
            }
        } catch (IOException e) {
            throw new ManageLoadException("Произошла ошибка во время загрузки файла.");
        }

        taskManager.updateEpicsSubTasksIds();
        taskManager.updateEpicsDurationStartEndTime();
        taskManager.setFile(file);
        return taskManager;
    }

    private void save() {
        if (fileToSave == null) {
            return;
        }

        try (FileWriter fileWriter = new FileWriter(fileToSave)) {
            String csvHeader = "id,type,name,description,status,start,duration,epic";
            fileWriter.write(csvHeader + eol);

            for (List<? extends Task> target : List.of(getTasks(), getSubTasks(), getEpics())) {
                for (Task task : target) {
                    fileWriter.write(toString(task));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    private <T extends Task> String toString(T task) {
        String epicId = "";
        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }

        String start = "";
        if (task.getStartTime() != null) {
            start = task.getStartTime().format(dateTimeFormatter);
        }

        long duration = 0;
        if (task.getDuration().isZero()) {
            duration = task.getDuration().toMinutes();
        }

        return task.id
                + csvSplitter
                + task.getType()
                + csvSplitter
                + task.getName()
                + csvSplitter
                + task.getText()
                + csvSplitter
                + task.getStatus()
                + csvSplitter
                + start
                + csvSplitter
                + duration
                + csvSplitter
                + epicId
                + csvSplitter
                + eol;
    }

    private Object fromString(String line) {
        String[] args = line.trim().split(csvSplitter);

        int id = Integer.valueOf(args[0]);
        TaskType type = TaskType.valueOf(args[1]);
        String name = args[2];
        String text = args[3];
        Status status = Status.valueOf(args[4]);
        LocalDateTime startTime = LocalDateTime.parse(args[5], dateTimeFormatter);
        int durationMinutes = Integer.valueOf(args[6]);

        switch (type) {
            case TASK:
                Task task = new Task(id);
                task.setName(name)
                        .setText(text)
                        .setStatus(status)
                        .setStartTime(startTime)
                        .setDuration(Duration.ofMinutes(durationMinutes));
                return task;
            case SUBTASK:
                int epicId = Integer.valueOf(args[7]);

                SubTask subTask = new SubTask(id);
                subTask.setEpicId(epicId)
                        .setName(name)
                        .setText(text)
                        .setStatus(status)
                        .setStartTime(startTime)
                        .setDuration(Duration.ofMinutes(durationMinutes));
                return subTask;
            case EPIC:
                Epic epic = new Epic(id);
                epic.setName(name)
                        .setText(text);
                return epic;
        }
        return null;
    }
}
