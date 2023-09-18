package com.example.demo.service;

import com.example.demo.enums.EmploymentStatus;
import com.example.demo.model.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanEligibilityServiceImplTest {

    private LoanEligibilityServiceImpl loanEligibilityService = new LoanEligibilityServiceImpl();

    @Test
    void testCheckLoanEligibilitySuccessScenario() {
        Customer customer = Customer.builder()
                .creditScore(750)
                .employmentStatus(EmploymentStatus.FULL_TIME).build();

        assertTrue(loanEligibilityService.checkLoanEligibility(customer));
    }

    @Test
    void testCheckLoanEligibilityWhenCustomerWorksPartTime() {

        Customer customer = Customer.builder()
                .creditScore(750)
                .employmentStatus(EmploymentStatus.PART_TIME).build();

        assertFalse(loanEligibilityService.checkLoanEligibility(customer));
    }


    @Test
    void testCheckLoanEligibilityWhenCreditScoreIsLess() {

        Customer customer = Customer.builder()
                .creditScore(600)
                .employmentStatus(EmploymentStatus.FULL_TIME).build();

        assertFalse(loanEligibilityService.checkLoanEligibility(customer));
    }
}

