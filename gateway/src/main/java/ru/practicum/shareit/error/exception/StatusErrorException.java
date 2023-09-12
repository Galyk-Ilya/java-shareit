package ru.practicum.shareit.error.exception;

public class StatusErrorException extends RuntimeException {
    public StatusErrorException(final String message) {
        super(message);
    }
}