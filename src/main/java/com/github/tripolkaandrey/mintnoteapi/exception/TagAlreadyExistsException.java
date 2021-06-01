package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends ApiException{
    private static final String ERROR_MESSAGE = "Tag already exists";

    public TagAlreadyExistsException() {
        super(HttpStatus.CONFLICT, ERROR_MESSAGE);
    }
}