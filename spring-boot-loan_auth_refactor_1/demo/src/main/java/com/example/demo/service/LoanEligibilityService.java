package com.example.demo.service;

import com.example.demo.model.Customer;

public interface LoanEligibilityService {
    boolean checkLoanEligibility(Customer customer);
}
