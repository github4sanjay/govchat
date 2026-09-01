package com.govtech.messaging.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError badRequest(IllegalArgumentException exception) {
        return new ApiError("INVALID_REQUEST", exception.getMessage(), Instant.now());
    }

    record ApiError(String code, String message, Instant timestamp) {}
}
