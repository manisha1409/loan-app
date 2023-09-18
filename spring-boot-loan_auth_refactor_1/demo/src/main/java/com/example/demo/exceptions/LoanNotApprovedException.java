package com.example.demo.exceptions;

public class LoanNotApprovedException extends RuntimeException {

    public LoanNotApprovedException() {
        super("Loan is yet to be approved.");
    }
}
