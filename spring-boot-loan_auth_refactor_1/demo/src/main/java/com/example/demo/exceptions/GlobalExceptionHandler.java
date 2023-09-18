package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Required request header 'Authorization' is missing");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CustomerNotEligibleException.class)
    public ResponseEntity<String> handleCustomerNotEligibleException(CustomerNotEligibleException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}

