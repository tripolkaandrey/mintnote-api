package com.github.tripolkaandrey.mintnoteapi.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
    private final String message;
    private final HttpStatus statusCode;

    protected ApiException(HttpStatus status, String message) {
        this.message = message;
        this.statusCode = status;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public ResponseBody getResponseBody() {
        return new ResponseBody();
    }

    private class ResponseBody {
        private final int statusCode;
        private final String message;

        private ResponseBody() {
            this.message = ApiException.this.message;
            this.statusCode = ApiException.this.statusCode.value();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }
    }
}
