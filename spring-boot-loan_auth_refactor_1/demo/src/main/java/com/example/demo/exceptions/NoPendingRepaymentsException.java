package com.example.demo.exceptions;

public class NoPendingRepaymentsException extends RuntimeException {

    public NoPendingRepaymentsException() {
        super("No pending repayments for the loan.");
    }
}
