package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public final class NoteNotFoundException extends ApiException {
    private static final String ERROR_MESSAGE = "Note with provided id was not found";

    public NoteNotFoundException() {
        super(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
    }
}