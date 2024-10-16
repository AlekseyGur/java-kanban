
public class Task {
    private static int globalId = 0; // значение для генерации id экземпляров
    public final int id; // id экземпляра задачи
    public final String text; // текст задачи
    private Status status;

    public Task(String text) {
        this.text = text;
        status = Status.NEW;
        id = ++globalId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toString() {
        return "Task [id=" + id + ", text=" + text + ", status=" + status + "]";
    }
}
