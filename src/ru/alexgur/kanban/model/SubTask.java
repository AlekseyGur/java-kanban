package ru.alexgur.kanban.model;

import ru.alexgur.kanban.service.TaskType;

public class SubTask extends Task {
    private int epicId = -1;

    public SubTask() {
        super();
    }

    public SubTask(int id) {
        super(id);
    }

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
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        String start = "";
        if (getStartTime() != null) {
            start = getStartTime().format(dateTimeFormatter);
        }

        long durationStr = 0;
        if (getDuration().isZero()) {
            durationStr = getDuration().toMinutes();
        }

        return "Task [id=" + id +
                ", type=" + getType() +
                ", name=" + getName() +
                ", text=" + getText() +
                ", status=" + getStatus() +
                ", startTime=" + start +
                ", duration=" + durationStr +
                ", epycId=" + epicId +
                "]";
    }
}