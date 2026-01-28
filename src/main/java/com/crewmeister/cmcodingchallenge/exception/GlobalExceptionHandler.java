package com.crewmeister.cmcodingchallenge.exception;

import com.crewmeister.cmcodingchallenge.network.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException appException,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(appException.getStatus().value())
                .error(appException.getStatus().getReasonPhrase())
                .message(appException.getMessage())
                .path(request.getRequestURI())
                .build();

        log.error(appException.getMessage(), appException);

        return new ResponseEntity<>(errorResponse, appException.getStatus());
    }

    @ExceptionHandler(BundesbankExchangeRateException.class)
    public ResponseEntity<ErrorResponse> handleBundesbankExchangeRateException(
            BundesbankExchangeRateException bundesbankExchangeRateException,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse;
        int status;

        try {
            JsonNode root = objectMapper.readTree(bundesbankExchangeRateException.getMessage());

            status = root.path("status").asInt(500);
            String message = root.path("title").asText("An unexpected error occurred");

            errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .error(HttpStatus.valueOf(status).getReasonPhrase())
                    .message(message)
                    .path(request.getRequestURI())
                    .build();

            log.error(message, bundesbankExchangeRateException);

        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Could not deserialize error response", jsonProcessingException);

            return handleSerializationException(
                    new SerializationException(
                            "Could not deserialize error response",
                            jsonProcessingException
                    ),
                    request
            );
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(status));
    }

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

        log.error(serializationException.getMessage(), serializationException);

        return new ResponseEntity<>(errorResponse, serializationException.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid input format")
                .message(methodArgumentTypeMismatchException.getMessage())
                .path(request.getRequestURI())
                .build();

        if (methodArgumentTypeMismatchException.getRequiredType() == LocalDate.class) {
            errorResponse.setMessage(methodArgumentTypeMismatchException.getValue() + " is invalid, format should be (YYYY-MM-DD)");
        }

        log.error(methodArgumentTypeMismatchException.getMessage(),  methodArgumentTypeMismatchException);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException missingServletRequestParameterException,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Missing Parameter")
                .message("Parameter " + missingServletRequestParameterException.getParameterName() + " is missing")
                .path(request.getRequestURI())
                .build();

        log.error(missingServletRequestParameterException.getParameterName(), missingServletRequestParameterException);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}