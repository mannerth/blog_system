package com.example.blog_system_backend.common;

import java.time.Instant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        String message = error != null ? error.getDefaultMessage() : "validation failed";

        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                Instant.now()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                Instant.now()
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage() != null ? ex.getMessage() : "invalid username or password",
                Instant.now()
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage() != null ? ex.getMessage() : "access denied",
                Instant.now()
        ));
    }
}
