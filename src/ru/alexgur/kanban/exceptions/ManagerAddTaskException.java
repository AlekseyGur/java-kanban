package ru.alexgur.kanban.exceptions;

public class ManagerAddTaskException extends RuntimeException {
    public ManagerAddTaskException(final String message) {
        super(message);
    }
}
