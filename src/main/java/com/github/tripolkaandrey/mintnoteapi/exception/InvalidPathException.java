package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public class InvalidPathException extends ApiException {
    private static final String ERROR_MESSAGE = "Invalid path was provided";

    public InvalidPathException() {
        super(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
    }
}