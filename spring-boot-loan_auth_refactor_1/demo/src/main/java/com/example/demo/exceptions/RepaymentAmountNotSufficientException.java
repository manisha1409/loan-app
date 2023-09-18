package com.example.demo.exceptions;

public class RepaymentAmountNotSufficientException extends RuntimeException {

    public RepaymentAmountNotSufficientException() {
        super("Repayment amount is not sufficient.");
    }


}
