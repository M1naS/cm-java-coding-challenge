package com.crewmeister.cmcodingchallenge.exception;

import com.crewmeister.cmcodingchallenge.network.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SerializationException.class)
    public ResponseEntity<ErrorResponse> handleSerializationException(
            SerializationException serializationException,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(serializationException.getStatus().value())
                .error(serializationException.getStatus().getReasonPhrase())
                .message(serializationException.getMessage())
                .path(request.getRequestURI())
                .build();

        log.error(serializationException.getMessage());

        return new ResponseEntity<>(errorResponse, serializationException.getStatus());
    }
}