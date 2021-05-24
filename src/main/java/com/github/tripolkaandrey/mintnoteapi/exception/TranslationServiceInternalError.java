package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public final class TranslationServiceInternalError extends ApiException {
    private static final String ERROR_MESSAGE = "Translation service encountered internal error";

    public TranslationServiceInternalError() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
    }
}