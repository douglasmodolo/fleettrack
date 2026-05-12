package com.fleettrack.order.presentation;

import com.fleettrack.order.domain.exception.OrderNotFoundException;
import com.fleettrack.order.presentation.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<String> errors = new ArrayList<>();
        for (var error : fieldErrors) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        var response = new ErrorResponse(
                400,
                "Validation failed",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        var response = new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(404).body(response);
    }
}
