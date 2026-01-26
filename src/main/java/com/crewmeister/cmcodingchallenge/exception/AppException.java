package com.crewmeister.cmcodingchallenge.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppException extends RuntimeException {
    private HttpStatus status;

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AppException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
