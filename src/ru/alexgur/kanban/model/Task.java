package ru.alexgur.kanban.model;

import ru.alexgur.kanban.service.Status;
import ru.alexgur.kanban.service.TaskType;

public class Task {
    private static int globalId = 0; // значение для генерации id экземпляров
    public final int id; // id экземпляра задачи
    private String name; // название задачи
    private String text; // текст задачи
    private Status status; // статус задачи

    public Task() {
        this.status = Status.NEW;
        id = ++globalId;
    }

    public Task(int id) {
        this.status = Status.NEW;
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
        return "Task [id=" + id + ", name=" + getName() + ", text=" + getText() + ", status=" + getStatus() + "]";
    }
}
