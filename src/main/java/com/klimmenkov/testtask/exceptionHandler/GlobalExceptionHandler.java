package com.klimmenkov.testtask.exceptionHandler;

import com.klimmenkov.testtask.error.ApiError;
import com.klimmenkov.testtask.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.NOT_FOUND.value());
        apiError.setDetail(ex.getMessage());
        apiError.setCode(404);
        apiError.setErrors(Collections.singletonList(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleAgeNotAllowedException(ConstraintViolationException ex) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setDetail(ex.getMessage());
        apiError.setCode(400);
        apiError.setErrors(Collections.singletonList(ex.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setDetail("Watch errors list");
        apiError.setCode(400);

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        apiError.setErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}