package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public class DirectoryNotFoundException extends ApiException {
    private static final String ERROR_MESSAGE = "Directory with provided path does not exist";

    public DirectoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
    }
}