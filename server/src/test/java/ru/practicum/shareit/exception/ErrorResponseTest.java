package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorResponseTest {

    private final ErrorHandler errorHandler;

    @Test
    void getNotFoundException() {
        ErrorResponse error = errorHandler.handleNotFoundException(new NotFoundException("message"));
        Assertions.assertEquals(error.getError(), "");
    }

    @Test
    void getAccessErrorException() {
        ErrorResponse error = errorHandler.handleAccessErrorException(new AccessErrorException("message"));
        Assertions.assertEquals(error.getError(), "Not enough rights.");
    }

    @Test
    void getStatusErrorException() {
        ErrorResponse error = errorHandler.handleStatusErrorException(new StatusErrorException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void getIncorrectDateError() {
        ErrorResponse error = errorHandler.handleIncorrectDateError(new IncorrectDateError("message"));
        Assertions.assertEquals(error.getError(), "Check your input. The incoming data is not correct.");
    }
}