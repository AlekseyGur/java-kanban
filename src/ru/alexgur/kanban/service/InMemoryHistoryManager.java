package ru.alexgur.kanban.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ru.alexgur.kanban.model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private HashMap<Integer, Node> history = new HashMap<>();

    public void clear() {
        clearImpl();
    }

    @Override
    public void remove(int id) {
        removeImpl(id);
    }

    @Override
    public void add(Task task) {
        addImpl(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> listTasks = getTasks().stream()
                .map(x -> x.getTask()).toList();
        return listTasks;
    }

    private Node linkLast(Task task) {
        final Node oldtail = tail;
        final Node newNode = new Node(oldtail, task, null);
        tail = newNode;
        if (oldtail == null) {
            head = newNode;
        } else {
            oldtail.setNext(newNode);
        }
        return newNode;
    }

    private ArrayList<Node> getTasks() {
        ArrayList<Node> nodes = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            nodes.add(currentNode);
            currentNode = currentNode.getNext();
        }
        Collections.reverse(nodes);
        return nodes;
    }

    private void addImpl(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                Node node = history.remove(task.getId());
                removeNode(node);
            }
            Node node = linkLast(task);
            history.put(task.getId(), node);
        }
    }

    private void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev != null && next != null) {
            // Удаляетяся элемент в середине списка
            next.setPrev(prev);
            prev.setNext(next);
        } else if (prev == null && next != null) {
            // Удаляетяся первый элемент
            head = next;
            next.setPrev(null);
        } else if (prev != null && next == null) {
            // Удаляетяся последний элемент
            tail = prev;
            prev.setNext(null);
        }

        history.remove(node.getTask().getId());
    }

    private void removeImpl(int id) {
        history.remove(id);
    }

    private void clearImpl() {
        head = null;
        tail = null;
        history.clear();
    }

    private class Node {
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

}
