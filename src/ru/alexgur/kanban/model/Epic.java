package ru.alexgur.kanban.model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private List<Integer> subTasksIds = new ArrayList<>();

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
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
    public String toString() {
        return "Task [id=" + id + ", name=" + getName() + ", text=" + getText() + ", status=" + getStatus()
                + ", subTasksIds=" + subTasksIds + "]";
    }

}
