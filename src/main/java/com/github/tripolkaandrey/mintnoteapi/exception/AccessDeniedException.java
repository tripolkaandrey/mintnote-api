package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public final class AccessDeniedException extends ApiException {
    private static final String ERROR_MESSAGE = "You are not allowed to access this resource";

    public AccessDeniedException() {
        super(HttpStatus.FORBIDDEN, ERROR_MESSAGE);
    }
}