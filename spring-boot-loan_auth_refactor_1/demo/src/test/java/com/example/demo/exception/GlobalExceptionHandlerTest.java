package com.example.demo.exception;

import com.example.demo.exceptions.CustomerNotEligibleException;
import com.example.demo.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleMissingServletRequestParameterException() {
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("param", "String");

        ResponseEntity<String> responseEntity = globalExceptionHandler.handleMissingServletRequestParameterException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), responseEntity.getBody());
    }

    @Test
    void handleCustomerNotEligibleException() {
        CustomerNotEligibleException exception = new CustomerNotEligibleException();

        ResponseEntity<String> responseEntity = globalExceptionHandler.handleCustomerNotEligibleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), responseEntity.getBody());
    }
}
