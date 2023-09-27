package com.klimmenkov.testtask.exception;

public class AgeNotAllowedException extends RuntimeException {

    public AgeNotAllowedException(String message) {
        super(message);
    }
}
