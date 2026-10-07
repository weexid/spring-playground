package com.weex.spring_playground;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.weex.spring_playground.config.common.ApiErrorResponse;
import com.weex.spring_playground.config.common.ConflictException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
        MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        // add logger
        logger.warn("Validation failed: {}", errors);

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validasi gagal",
                exception.getParameter().getMethod().getName(),
                errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler (DataIntegrityViolationException.class) 
    public ResponseEntity<?> handleConflict(DataIntegrityViolationException exception, HttpServletRequest req) {
        logger.warn("Conflict DB constraint: {}", exception.getMostSpecificCause().getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "Terjadi konflik data",
                req.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler (ConflictException.class)
        public ResponseEntity<?> handleConflict(ConflictException exception, HttpServletRequest req) {
        logger.warn("Conflict: {}", exception.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                req.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler (NoResourceFoundException.class)
    public ResponseEntity<?> handleNotFound(NoResourceFoundException exception, HttpServletRequest req) {
        logger.warn("Resource not found: {}", req.getRequestURI());
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Resource tidak ditemukan",
                req.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }           
}