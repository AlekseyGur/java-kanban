package ru.alexgur.kanban.model;

public class SubTask extends Task {

    public final int epicId;

    public SubTask(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", name=" + getName() + ", text=" + getText() + ", status=" + getStatus()
                + ", epycId=" + epicId + "]";
    }
}