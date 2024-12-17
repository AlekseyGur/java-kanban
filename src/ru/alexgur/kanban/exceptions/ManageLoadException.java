package ru.alexgur.kanban.exceptions;

public class ManageLoadException extends RuntimeException {
    public ManageLoadException(final String message) {
        super(message);
    }
}
