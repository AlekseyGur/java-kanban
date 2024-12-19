package ru.alexgur.kanban.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskType;

public class Task {
    private static int globalId = 0; // значение для генерации id экземпляров
    public final int id; // id экземпляра задачи
    private String name; // название задачи
    private String text; // текст задачи
    private Status status; // статус задачи
    private Duration duration = Duration.ZERO; // продолжительность задачи в минутах
    private LocalDateTime startTime; // дата и время старта выполнения задачи
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Task setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Task setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public Task() {
        this.status = Status.NEW;
        id = ++globalId;
    }

    public Task(int id) {
        this.status = Status.NEW;
        if (globalId < id) {
            globalId = id;
        }
        this.id = id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public Task setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getName() {
        return name;
    }

    public Task setName(String name) {
        this.name = name;
        return this;
    }

    public String getText() {
        return text;
    }

    public static <T extends Task> int compareToStartTimeAsc(T a, T b) {
        return a.getStartTime().isBefore(b.getStartTime()) ? -1 : 1;
    }

    public Task setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Task other = (Task) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else if (!text.equals(other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String start = "";
        if (getStartTime() != null) {
            start = getStartTime().format(DATE_TIME_FORMATTER);
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
                "]";
    }
}
