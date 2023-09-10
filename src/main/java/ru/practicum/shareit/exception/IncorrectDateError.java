package ru.practicum.shareit.exception;

public class IncorrectDateError extends RuntimeException {
    public IncorrectDateError(final String message) {
        super(message);
    }
}