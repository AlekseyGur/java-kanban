package ru.alexgur.kanban.model;

import java.util.List;
import java.util.ArrayList;

import ru.alexgur.kanban.service.Status;

public class Epic extends Task {

    private List<Integer> subTasksIds = new ArrayList<>();

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(List<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    public void setStatus() {
        // Нельзя установить статус эпика вручную. Надо запускать
        // его вычисление через updateStatus(List<SubTask> subTasks)
        return;
    }

    public void updateStatus(List<SubTask> subTasks) {
        boolean isNew = true;
        boolean isDone = true;

        for (SubTask t : subTasks) {
            if (t.getStatus() != Status.NEW)
                isNew = false;
            if (t.getStatus() != Status.DONE)
                isDone = false;
        }

        Status newStatus;
        if (isNew)
            newStatus = Status.NEW; // все подзадачи имеют статус NEW или их нет
        else if (isDone)
            newStatus = Status.DONE; // все подзадачи имеют DONE
        else
            newStatus = Status.IN_PROGRESS;

        this.setStatus(newStatus);
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", name=" + getName() + ", text=" + getText() + ", status=" + getStatus()
                + ", subTasksIds=" + subTasksIds + "]";
    }

}
