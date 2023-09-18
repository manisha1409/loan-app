package com.example.demo.enums;

import java.math.BigDecimal;

public enum LoanType {

    HOME(0.0),
    EDUCATION(0.0),
    CAR(0.0);

    private final double annualInterestRate;

    LoanType(double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public BigDecimal getAnnualInterestRate() {
        return new BigDecimal(annualInterestRate);
    }
}
