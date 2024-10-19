package ru.alexgur.kanban.model;

public class SubTask extends Task {

    public final int epycId;

    public SubTask(int epycId) {
        this.epycId = epycId;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", name=" + getName() + ", text=" + getText() + ", status=" + getStatus()
                + ", epycId=" + epycId + "]";
    }
}