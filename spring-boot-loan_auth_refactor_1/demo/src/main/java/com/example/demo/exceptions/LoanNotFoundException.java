package com.example.demo.exceptions;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException() {
        super("Loan not found.");
    }
}
