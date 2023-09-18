package com.example.demo.service;

import com.example.demo.enums.EmploymentStatus;
import com.example.demo.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class LoanEligibilityServiceImpl implements LoanEligibilityService {

    private static final int MIN_CREDIT_SCORE = 700;
    private static final EmploymentStatus EMPLOYMENT_STATUS = EmploymentStatus.FULL_TIME;

    @Override
    public boolean checkLoanEligibility(Customer customer) {

        return customer.getCreditScore() >= MIN_CREDIT_SCORE &&
                customer.getEmploymentStatus() == EMPLOYMENT_STATUS ;
    }


}
