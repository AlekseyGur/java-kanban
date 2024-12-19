package ru.alexgur.kanban.model;

import java.util.List;

import ru.alexgur.kanban.service.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Integer> subTasksIds = new ArrayList<>();
    private LocalDateTime endTime; // дата и время завершения всех задач

    public Epic() {
        super();
    }

    public Epic(int id) {
        super(id);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setSubTasksIds(List<Integer> subTasksIds) {
        List<Integer> subTasksIdsFiltered = new ArrayList<>();
        for (Integer id : subTasksIds) {
            if (id != this.id) {
                subTasksIdsFiltered.add(id);
            }
        }
        this.subTasksIds = subTasksIdsFiltered;
    }

    public void deleteSubTasks() {
        subTasksIds.clear();
    }

    public void deleteSubTask(Integer subTasksId) {
        subTasksIds.remove(subTasksId);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
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
                ", subTasksIds=" + subTasksIds +
                "]";
    }
}
