package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public class DirectoryAlreadyExistsException extends ApiException {
    private static final String ERROR_MESSAGE = "Directory already exists";

    public DirectoryAlreadyExistsException() {
        super(HttpStatus.CONFLICT, ERROR_MESSAGE);
    }
}