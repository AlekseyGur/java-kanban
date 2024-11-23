package ru.alexgur.kanban.service;

import ru.alexgur.kanban.model.Task;

public class Node {
    private Task task;
    private Node next;
    private Node prev;

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    } 

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Node other = (Node) obj;
        return task.equals(other.task);
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
