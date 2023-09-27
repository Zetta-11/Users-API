package com.klimmenkov.testtask.exceptionHandler;

import com.klimmenkov.testtask.error.ApiError;
import com.klimmenkov.testtask.exception.AgeNotAllowedException;
import com.klimmenkov.testtask.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setDetail(ex.getMessage());
        apiError.setCode(404);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(AgeNotAllowedException.class)
    public ResponseEntity<ApiError> handleAgeNotAllowedException(AgeNotAllowedException ex) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setDetail(ex.getMessage());
        apiError.setCode(403);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}