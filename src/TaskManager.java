import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    // Ключ хеша совпадает с Task.id, используется для быстрого поиска
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<SubTask> getAllSubTasksByEpic(Task epic) {
        ArrayList<SubTask> sub = new ArrayList<>();

        for (SubTask t : subTasks.values())
            if (t.parentId == epic.id)
                sub.add(t);

        return sub;
    }

    public Epic addEpic(String text) {
        Epic t = new Epic(text);
        epics.put(t.id, t);
        return t;
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    public Epic getEpicById(int id) {
        if (!epics.containsKey(id))
            return null;
        return epics.get(id);
    }

    public SubTask addSubTask(Epic epic, String text) {
        SubTask task = new SubTask(epic, text);
        subTasks.put(task.id, task);
        updateStatusEpicById(task.parentId);
        return task;
    }

    public SubTask setSubTaskStatus(int id, Status status) {
        SubTask task = getSubTaskById(id);
        task.setStatus(status);
        updateSubTask(task);
        return task;
    }

    public void updateSubTask(SubTask task) {
        subTasks.put(task.id, task);
        updateStatusEpicById(task.parentId);
    }

    public SubTask getSubTaskById(int id) {
        for (SubTask t : subTasks.values())
            if (t.id == id)
                return t;
        return null;
    }

    public void deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            int parentId = subTasks.get(id).parentId;
            subTasks.remove(id);
            updateStatusEpicById(parentId);
        }
    }

    public void deleteAllSubTasksByEpic(Epic epic) {
        for (SubTask t : getAllSubTasksByEpic(epic))
            deleteSubTaskById(t.id);
    }

    public void updateStatusEpicById(int id) {
        boolean isNew = true;
        boolean isDone = true;

        Epic epic = getEpicById(id);

        for (SubTask t : subTasks.values()) {
            if (t.parentId == epic.id) {
                if (t.getStatus() != Status.NEW)
                    isNew = false;
                if (t.getStatus() != Status.DONE)
                    isDone = false;
            }
        }

        Status newStatus;
        if (isNew)
            newStatus = Status.NEW; // все подзадачи имеют статус NEW или их нет
        else if (isDone)
            newStatus = Status.DONE; // все подзадачи имеют DONE
        else
            newStatus = Status.IN_PROGRESS;

        epic.setStatus(newStatus);
        updateEpic(epic);
    }

    public void printAll() {
        for (Epic e : epics.values()) {
            System.out.println("Эпик: " + e);

            ArrayList<SubTask> st = getAllSubTasksByEpic(e);
            if (st.isEmpty()) {
                System.out.println("Не содержит задач.");
            } else {
                System.out.println("Задачи:");
                for (SubTask s : getAllSubTasksByEpic(e)) {
                    System.out.println(" - " + s);
                }
            }
            System.out.println();
        }
    }

    public void deleteEpic(Epic epic) {
        // удаляем все задачи из эпика, если они есть
        deleteAllSubTasksByEpic(epic);

        // удаляем сам эпик epic
        if (epics.containsKey(epic.id)) {
            epics.remove(epic.id);
        }
    }
}
