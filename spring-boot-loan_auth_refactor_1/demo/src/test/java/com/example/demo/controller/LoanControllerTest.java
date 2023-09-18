package com.example.demo.controller;

import com.example.demo.enums.LoanType;
import com.example.demo.model.Loan;
import com.example.demo.service.AuthorizationService;
import com.example.demo.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanControllerTest {


    private LoanService loanService = mock(LoanService.class);
    private AuthorizationService authorizationService = mock(AuthorizationService.class);

    private LoanController loanController  = new LoanController(loanService,authorizationService);


    @Test
    void createLoanWithValidAuthorizationSuccessScenario() {
        //given
        Long customerId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1000);
        int term = 12;
        LoanType loanType = LoanType.EDUCATION;
        String authorization = "ValidAuthorizationToken";
        Loan expectedLoan = Loan.builder()
                .loanType(loanType)
                .term(12)
                .amount(amount)
                .build();

        when(authorizationService.isCustomerAuthorized(authorization)).thenReturn(true);
        when(loanService.createLoanForCustomer(customerId, amount, term, loanType, LocalDate.now())).thenReturn(expectedLoan);

        //when
        ResponseEntity<Object> responseEntity = loanController.createLoan(customerId, amount, term, loanType, authorization);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedLoan, responseEntity.getBody());
    }

    @Test
    void createLoanWithValidAuthorizationFailureScenario() {
        // given
        Long customerId = 1L;
        BigDecimal amount = BigDecimal.valueOf(1000);
        int term = 12;
        LoanType loanType = LoanType.CAR;
        String authorization = "InvalidAuthorizationToken";

        when(authorizationService.isCustomerAuthorized(authorization)).thenReturn(false);

        //when
        ResponseEntity<Object> responseEntity = loanController.createLoan(customerId, amount, term, loanType, authorization);

        //then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Invalid credentials", responseEntity.getBody());
    }


    @Test
    void approveLoanWithValidAuthorizationSuccessScenario() {
        // given
        Long loanId = 1L;
        String authorization = "ValidAuthorizationToken";
        Loan expectedLoan = Loan.builder().build();

        when(authorizationService.isAdminAuthorized(authorization)).thenReturn(true);
        when(loanService.approveLoan(loanId)).thenReturn(expectedLoan);

        //when
        ResponseEntity<Object> responseEntity = loanController.approveLoan(loanId, authorization);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedLoan, responseEntity.getBody());
    }

    @Test
    void addRepaymentWithValidAuthorizationSuccessScenario() {
        // given
        Long loanId = 1L;
        BigDecimal repaymentAmount = BigDecimal.valueOf(500);
        String authorization = "ValidAuthorizationToken";
        Loan expectedLoan = Loan.builder().build();

        when(authorizationService.isCustomerAuthorized(authorization)).thenReturn(true);
        when(loanService.addRepayment(loanId, repaymentAmount)).thenReturn(expectedLoan);

        // when
        ResponseEntity<Object> responseEntity = loanController.addRepayment(loanId, repaymentAmount, authorization);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedLoan, responseEntity.getBody());
    }



    @Test
    void loanByCustomerIdWithInValidAuthorizationFailureScenario() {
        // given
        Long customerId = 1L;
        String authorization = "InvalidAuthorizationToken";

        when(authorizationService.isCustomerAuthorized(authorization)).thenReturn(false);

        // when
        ResponseEntity<Object> responseEntity = loanController.loanByCustomerId(customerId, authorization);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}

