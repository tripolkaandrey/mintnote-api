package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public final class ApiExceptionHandler {

    @ExceptionHandler
    protected Mono<ResponseEntity<Object>> handleApiException(ApiException ex) {
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBody()));
    }
}