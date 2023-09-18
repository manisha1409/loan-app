package com.example.demo.exceptions;

public class   CustomerNotEligibleException extends RuntimeException {

    public CustomerNotEligibleException() {
        super("Customer is not eligible for loan");
    }
}
