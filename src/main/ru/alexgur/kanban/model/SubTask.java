package main.ru.alexgur.kanban.model;

public class SubTask extends Task {

    private int epicId = -1;

    public SubTask setEpicId(int epicId) {
        if (this.epicId == -1 && this.id != epicId) {
            this.epicId = epicId;
        }
        return this;
    }

    public int getEpicId() {
        return this.epicId;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", name=" + getName() + ", text=" + getText() + ", status=" + getStatus()
                + ", epycId=" + epicId + "]";
    }
}