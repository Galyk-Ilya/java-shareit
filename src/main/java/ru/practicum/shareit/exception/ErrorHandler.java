package ru.practicum.shareit.exception;

import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlerNotFoundException(final NotFoundException e) {
        log.info("Error 404: {}", e.getMessage());
            return Map.of("Object not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handlerInternalException(final ExecutionControl.InternalException e) {
        log.info("Error 500: {}", e.getMessage());
        return Map.of("server error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlerDublicateException(final DublicateException e) {
        log.info("Error 409: {}", e.getMessage());
        return Map.of("Error - data conflict", e.getMessage());
    }
}