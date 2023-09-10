package ru.practicum.shareit.exception;

public class StatusErrorException extends RuntimeException {
    public StatusErrorException(final String message) {
        super(message);
    }
}