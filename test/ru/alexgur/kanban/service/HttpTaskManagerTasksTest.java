package test.ru.alexgur.kanban.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import ru.alexgur.kanban.adapters.EpicListTypeToken;
import ru.alexgur.kanban.adapters.EpicTypeToken;
import ru.alexgur.kanban.adapters.SubTaskListTypeToken;
import ru.alexgur.kanban.adapters.SubTaskTypeToken;
import ru.alexgur.kanban.adapters.TaskListTypeToken;
import ru.alexgur.kanban.adapters.TaskTypeToken;

import ru.alexgur.kanban.model.Epic;
import ru.alexgur.kanban.model.SubTask;
import ru.alexgur.kanban.model.Task;

import ru.alexgur.kanban.service.HttpTaskServer;
import ru.alexgur.kanban.service.InMemoryHistoryManager;
import ru.alexgur.kanban.service.InMemoryTaskManager;
import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskManager;

public class HttpTaskManagerTasksTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteTasks();
        taskManager.deleteSubTasks();
        taskManager.deleteEpics();
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task()
                .setName("Test 1")
                .setText("Testing task 1")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        Task task2 = new Task()
                .setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now().plusMinutes(50));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpResponse<String> response = makeRequest("GET", "/tasks");

        assertEquals(200, response.statusCode());

        List<Task> parsed = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 2);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Test 1"));
        assertTrue("Задачи не совпадают", parsed.get(0).equals(task1));
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task2 = new Task(2)
                .setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now().plusMinutes(50));

        taskManager.addTask(task2);

        HttpResponse<String> response = makeRequest("GET", "/tasks/2/");

        assertEquals(200, response.statusCode());

        Task parsed = gson.fromJson(response.body(), new TaskTypeToken().getType());

        assertTrue("Некорректное имя задачи", parsed.getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", parsed.equals(task2));
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task()
                .setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        HttpResponse<String> response = makeRequest("POST", "/tasks", task);

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTasks();
        Task savedTask = tasksFromManager.get(0);

        assertTrue("Задачи не возвращаются", tasksFromManager.size() > 0);
        assertTrue("Некорректное количество задач", tasksFromManager.size() == 1);
        assertTrue("Некорректное имя задачи", savedTask.getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", savedTask.equals(task));
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(1)
                .setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        taskManager.addTask(task);

        Task task2 = new Task(1)
                .setName("Test 3")
                .setText("Testing task 3")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now());

        makeRequest("POST", "/tasks/1/", task2);
        List<Task> tasksFromManager = taskManager.getTasks();
        Task savedTask = tasksFromManager.get(0);

        assertTrue("Задачи не возвращаются", tasksFromManager.size() > 0);
        assertTrue("Некорректное количество задач", tasksFromManager.size() == 1);
        assertTrue("Некорректное имя задачи", savedTask.getName().equals("Test 3"));
        assertTrue("Задачи не совпадают", savedTask.equals(task2));
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1)
                .setName("Test 1")
                .setText("Testing task 1")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        Task task2 = new Task(2)
                .setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now().plusMinutes(50));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpResponse<String> response = makeRequest("DELETE", "/tasks/1/");

        assertEquals(200, response.statusCode());

        List<Task> parsed = taskManager.getTasks();

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 1);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", parsed.get(0).equals(task2));
    }

    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        // создаём задачу
        SubTask task1 = new SubTask();
        task1.setName("Test 1")
                .setText("Testing task 1")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        SubTask task2 = new SubTask();
        task2.setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now().plusMinutes(50));

        taskManager.addSubTask(task1);
        taskManager.addSubTask(task2);

        HttpResponse<String> response = makeRequest("GET", "/subtasks");

        assertEquals(200, response.statusCode());

        List<SubTask> parsed = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 2);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Test 1"));
        assertTrue("Задачи не совпадают", parsed.get(0).equals(task1));
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        SubTask task2 = new SubTask(2);
        task2.setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now().plusMinutes(50));

        taskManager.addSubTask(task2);

        HttpResponse<String> response = makeRequest("GET", "/subtasks/2/");

        assertEquals(200, response.statusCode());

        SubTask parsed = gson.fromJson(response.body(), new SubTaskTypeToken().getType());

        assertTrue("Некорректное имя задачи", parsed.getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", parsed.equals(task2));
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        // создаём задачу
        SubTask task = new SubTask();
        task.setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        HttpResponse<String> response = makeRequest("POST", "/subtasks", task);

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = taskManager.getSubTasks();
        SubTask savedTask = tasksFromManager.get(0);

        assertTrue("Задачи не возвращаются", tasksFromManager.size() > 0);
        assertTrue("Некорректное количество задач", tasksFromManager.size() == 1);
        assertTrue("Некорректное имя задачи", savedTask.getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", savedTask.equals(task));
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        SubTask task = new SubTask(1);
        task.setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        taskManager.addSubTask(task);

        SubTask task2 = new SubTask(1);
        task2.setName("Test 3")
                .setText("Testing task 3")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now());

        makeRequest("POST", "/subtasks/1/", task2);
        List<SubTask> tasksFromManager = taskManager.getSubTasks();
        SubTask savedTask = tasksFromManager.get(0);

        assertTrue("Задачи не возвращаются", tasksFromManager.size() > 0);
        assertTrue("Некорректное количество задач", tasksFromManager.size() == 1);
        assertTrue("Некорректное имя задачи", savedTask.getName().equals("Test 3"));
        assertTrue("Задачи не совпадают", savedTask.equals(task2));
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        SubTask task1 = new SubTask(1);
        task1.setName("Test 1")
                .setText("Testing task 1")
                .setStatus(Status.NEW)
                .setDuration(Duration.ofMinutes(5))
                .setStartTime(LocalDateTime.now());

        SubTask task2 = new SubTask(2);
        task2.setName("Test 2")
                .setText("Testing task 2")
                .setStatus(Status.DONE)
                .setDuration(Duration.ofMinutes(15))
                .setStartTime(LocalDateTime.now().plusMinutes(50));

        taskManager.addSubTask(task1);
        taskManager.addSubTask(task2);

        HttpResponse<String> response = makeRequest("DELETE", "/subtasks/1/");

        assertEquals(200, response.statusCode());

        List<SubTask> parsed = taskManager.getSubTasks();

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 1);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", parsed.get(0).equals(task2));
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic task1 = new Epic();
        task1.setName("Test 1")
                .setText("Testing task 1")
                .setStartTime(LocalDateTime.now());

        Epic task2 = new Epic();
        task2.setName("Test 2")
                .setText("Testing task 2")
                .setStartTime(LocalDateTime.now());

        taskManager.addEpic(task1);
        taskManager.addEpic(task2);

        HttpResponse<String> response = makeRequest("GET", "/epics");

        assertEquals(200, response.statusCode());

        List<Epic> parsed = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 2);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Test 1"));
        assertTrue("Задачи не совпадают", parsed.get(0).equals(task1));
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic task2 = new Epic(2);
        task2.setName("Test 2")
                .setText("Testing task 2")
                .setStartTime(LocalDateTime.now());

        taskManager.addEpic(task2);

        HttpResponse<String> response = makeRequest("GET", "/epics/2/");

        assertEquals(200, response.statusCode());

        Epic parsed = gson.fromJson(response.body(), new EpicTypeToken().getType());

        assertTrue("Некорректное имя задачи", parsed.getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", parsed.equals(task2));
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic task = new Epic();
        task.setName("Test 2")
                .setText("Testing task 2")
                .setStartTime(LocalDateTime.now());

        HttpResponse<String> response = makeRequest("POST", "/epics", task);

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = taskManager.getEpics();
        Epic savedTask = tasksFromManager.get(0);

        assertTrue("Задачи не возвращаются", tasksFromManager.size() > 0);
        assertTrue("Некорректное количество задач", tasksFromManager.size() == 1);
        assertTrue("Некорректное имя задачи", savedTask.getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", savedTask.equals(task));
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic task = new Epic(1);
        task.setName("Test 2")
                .setText("Testing task 2")
                .setStartTime(LocalDateTime.now());

        taskManager.addEpic(task);

        Epic task2 = new Epic(1);
        task2.setName("Test 3")
                .setText("Testing task 3")
                .setStartTime(LocalDateTime.now());

        makeRequest("POST", "/epics/1/", task2);
        List<Epic> tasksFromManager = taskManager.getEpics();
        Epic savedTask = tasksFromManager.get(0);

        assertTrue("Задачи не возвращаются", tasksFromManager.size() > 0);
        assertTrue("Некорректное количество задач", tasksFromManager.size() == 1);
        assertTrue("Некорректное имя задачи", savedTask.getName().equals("Test 3"));
        assertTrue("Задачи не совпадают", savedTask.equals(task2));
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic task1 = new Epic(1);
        task1.setName("Test 1")
                .setText("Testing task 1");

        Epic task2 = new Epic(2);
        task2.setName("Test 2")
                .setText("Testing task 2")
                .setStartTime(LocalDateTime.now());

        taskManager.addEpic(task1);
        taskManager.addEpic(task2);

        HttpResponse<String> response = makeRequest("DELETE", "/epics/1/");

        assertEquals(200, response.statusCode());

        List<Epic> parsed = taskManager.getEpics();

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 1);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Test 2"));
        assertTrue("Задачи не совпадают", parsed.get(0).equals(task2));
    }

    @Test
    public void testGetEpicSubTasksById() throws IOException, InterruptedException {
        Epic epic = new Epic(2);
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();
        epic.setName("Epic").setText("Epic");
        subTask1.setName("SubTask 1").setText("SubTask Text 1");
        subTask2.setName("SubTask 2").setText("SubTask Text 2");

        epic.setSubTasksIds(List.of(subTask1.id, subTask2.id));

        subTask1.setEpicId(epic.id);
        subTask2.setEpicId(epic.id);

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addEpic(epic);

        HttpResponse<String> response = makeRequest("GET", "/epics/2/subtasks");

        assertEquals(200, response.statusCode());

        List<SubTask> parsed = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == 2);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("SubTask 1"));
        assertTrue("Задача 1 не совпадает", parsed.get(0).equals(subTask1));
        assertTrue("Задача 2 не совпадает", parsed.get(1).equals(subTask2));
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        clearHistory();
        int maxHistSize = 10;

        for (int i = 1; i <= maxHistSize; i++) {
            Task task = new Task().setName("Task " + i);
            taskManager.addTask(task);
            taskManager.getTask(task.id);
        }
        List<Task> savedHist = taskManager.getHistoryManager().getHistory();

        HttpResponse<String> response = makeRequest("GET", "/history");

        assertEquals(200, response.statusCode());

        List<Task> parsed = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertTrue("Задачи не возвращаются", parsed.size() > 0);
        assertTrue("Некорректное количество задач", parsed.size() == maxHistSize);
        assertTrue("Некорректное имя задачи", parsed.get(0).getName().equals("Task " + maxHistSize));
        assertTrue("Задача 1 не совпадает", savedHist.get(0).equals(parsed.get(0)));
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task();
        Task task2 = new Task();
        SubTask task3 = new SubTask();

        task1.setDuration(Duration.ofSeconds(60))
                .setStartTime(LocalDateTime.parse("2024-08-17 01:32:21",
                        Task.dateTimeFormatter));
        task2.setDuration(Duration.ofSeconds(60))
                .setStartTime(LocalDateTime.parse("2024-08-17 03:32:21",
                        Task.dateTimeFormatter));
        task3.setDuration(Duration.ofSeconds(60))
                .setStartTime(LocalDateTime.parse("2024-08-17 02:32:21",
                        Task.dateTimeFormatter));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        List<Task> savedTasks = taskManager.getPrioritizedTasks();

        HttpResponse<String> response = makeRequest("GET", "/prioritized");

        assertEquals(200, response.statusCode());

        List<Task> parsed = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(parsed.size(), 3, "Приоритетные задачи не добавляются");
        assertEquals(savedTasks.get(0), parsed.get(0), "Приоритетные задачи не сортируются по времени");
        assertEquals(savedTasks.get(1).getId(), parsed.get(1).getId(), "Приоритетные задачи не сортируются по времени");
        assertEquals(savedTasks.get(2).getId(), parsed.get(2).getId(), "Приоритетные задачи не сортируются по времени");
    }

    private <T> HttpResponse<String> makeRequest(String method, String endpoint, T obj)
            throws IOException, InterruptedException {

        // конвертируем её в JSON
        String taskJson = gson.toJson(obj);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://" + HttpTaskServer.IP + ":" + HttpTaskServer.PORT + endpoint);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(url);
        if (method.equals("POST")) {
            requestBuilder = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(taskJson));
        }
        if (method.equals("DELETE")) {
            requestBuilder = requestBuilder.DELETE();
        }
        if (method.equals("GET")) {
            requestBuilder = requestBuilder.header("Accept", "application/json").GET();
        }
        HttpRequest request = requestBuilder.build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response;
    }

    private <T> HttpResponse<String> makeRequest(String method, String endpoint)
            throws IOException, InterruptedException {
        return makeRequest(method, endpoint, null);
    }

    private void clearHistory() {
        InMemoryHistoryManager thm = (InMemoryHistoryManager) taskManager.getHistoryManager();
        thm.clear();
    }
}
