package ru.practicum.shareit.exception;

public class AccessErrorException extends RuntimeException {
    public AccessErrorException(final String message) {
        super(message);
    }
}