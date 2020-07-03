package ru.psu.pro_it_test.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.psu.pro_it_test.exceptions.BaseException;
import ru.psu.pro_it_test.exceptions.CannotBeDeletedException;

import java.net.ConnectException;

@ControllerAdvice
public class ExceptionController {
    private static ErrorResponseEntity createErrorResponseEntity(BaseException e, HttpStatus httpStatus) {
        return new ErrorResponseEntity(e.getMessage(), httpStatus.getReasonPhrase(), httpStatus.value());
    }

    @ExceptionHandler(CannotBeDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponseEntity handleCannotBeDeletedException(CannotBeDeletedException e) {
        return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
    }
}
