package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public class InvalidLanguageCodeException extends ApiException {
    private static final String ERROR_MESSAGE = "Invalid language code. " +
            "Please check out the list of supported language codes by Google Cloud Translation API";

    public InvalidLanguageCodeException() {
        super(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
    }
}