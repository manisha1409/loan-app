package com.example.demo.service;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.dao.LoanDAO;
import com.example.demo.dao.ScheduledRepaymentDAO;
import com.example.demo.enums.LoanStatus;
import com.example.demo.enums.LoanType;
import com.example.demo.enums.RepaymentState;
import com.example.demo.exceptions.*;
import com.example.demo.model.Customer;
import com.example.demo.model.Loan;
import com.example.demo.model.ScheduledRepayment;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanServiceTest {

    LoanDAO loanDAO = Mockito.mock(LoanDAO.class);
    ScheduledRepaymentDAO scheduledRepaymentDAO = Mockito.mock(ScheduledRepaymentDAO.class);
    CustomerDAO customerDAO = Mockito.mock(CustomerDAO.class);
    LoanEligibilityService loanEligibilityService = Mockito.mock(LoanEligibilityService.class);

    private LoanService loanService = new LoanService(loanDAO, scheduledRepaymentDAO, customerDAO, loanEligibilityService);


    @Test
    void testCreateLoanForCustomerSuccessScenario() {
        // given
        Customer customer = Customer.builder().id(1L).build();
        when(customerDAO.findById(1L)).thenReturn(Optional.of(customer));
        when(loanDAO.save(any())).thenReturn(Loan.builder().amount(BigDecimal.valueOf(1000)).loanType(LoanType.EDUCATION).state(LoanStatus.PENDING).term(12).build());
        //when
        Loan result = loanService.createLoanForCustomer(1L, BigDecimal.valueOf(1000), 12, LoanType.EDUCATION, LocalDate.now());

        // then
        assertNotNull(result);
        assertEquals(LoanStatus.PENDING, result.getState());
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        assertEquals(12, result.getTerm());
    }

    @Test
    void testCreateLoanForCustomerWhenCustomerIdIsNotCorrect() {
        // given
        when(customerDAO.findById(1L)).thenReturn(Optional.empty());
        Loan result = loanService.createLoanForCustomer(1L, BigDecimal.valueOf(1000), 12, LoanType.EDUCATION, LocalDate.now());

        // then
        assertNull(result);
    }

    @Test
    void testApproveLoanSuccessScenario() {
        // given
        Loan loan = Loan.builder().id(1L).state(LoanStatus.PENDING).build();

        when(loanDAO.findById(1L)).thenReturn(Optional.of(loan));
        when(loanDAO.save(any())).thenReturn(loan);
        when(loanEligibilityService.checkLoanEligibility(any())).thenReturn(true);

        // when
        Loan approvedLoan = loanService.approveLoan(1L);

        //then
        assertNotNull(approvedLoan);
        assertEquals(LoanStatus.APPROVED, approvedLoan.getState());
    }

    @Test
    void testApproveLoanWhenCustomerIsNotEligible() {
        // given
        Loan loan = Loan.builder().id(1L).state(LoanStatus.PENDING).build();
        when(loanDAO.findById(1L)).thenReturn(Optional.of(loan));
        when(loanDAO.save(any())).thenReturn(loan);
        when(loanEligibilityService.checkLoanEligibility(any())).thenReturn(false);

        // when and then
        assertThrows(CustomerNotEligibleException.class, () -> loanService.approveLoan(1L));
    }

    @Test
    void testApproveLoanLoanNotFound() {
        when(loanDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanService.approveLoan(1L));
    }

    @Test
    void testFetchLoansByCustomerId() {
        //given
        Long customerId = 1L;
        List<Loan> mockLoans = new ArrayList<>();
        mockLoans.add(Loan.builder().build());
        mockLoans.add(Loan.builder().build());
        when(loanDAO.findByCustomer_Id(customerId)).thenReturn(mockLoans);

        // when
        List<Loan> fetchedLoans = loanService.fetchLoansByCustomerId(customerId);

        //then
        assertEquals(2, fetchedLoans.size());
    }


    @Test
    void testAddRepaymentSuccessScenario() {

        //given
        Long loanId = 1L;
        BigDecimal repaymentAmount = new BigDecimal("100.00");
        Loan loan = Loan.builder().state(LoanStatus.APPROVED).id(loanId).build();
        ScheduledRepayment pendingRepayment = ScheduledRepayment.builder().state(RepaymentState.PENDING).id(loanId).paymentPending(new BigDecimal("100.00")).build();

        loan.setScheduledRepayments(Collections.singletonList(pendingRepayment));
        when(loanDAO.findById(loanId)).thenReturn(Optional.of(loan));

        // when
        Loan updatedLoan = loanService.addRepayment(loanId, repaymentAmount);

        //then
        assertEquals(RepaymentState.PAID, pendingRepayment.getState());
        assertEquals(BigDecimal.ZERO, pendingRepayment.getPaymentPending());
        assertEquals(LoanStatus.PAID, updatedLoan.getState());
    }

    @Test
    void testAddRepaymentForInsufficientRepaymentAmount() {
        //given
        Long loanId = 1L;
        Loan loan = Loan.builder().state(LoanStatus.APPROVED).id(loanId).build();
        ScheduledRepayment pendingRepayment = ScheduledRepayment.builder().state(RepaymentState.PENDING).id(loanId).paymentPending(new BigDecimal("100.00")).build();

        loan.setScheduledRepayments(Collections.singletonList(pendingRepayment));
        when(loanDAO.findById(loanId)).thenReturn(Optional.of(loan));


        // when and then
        assertThrows(RepaymentAmountNotSufficientException.class, () -> loanService.addRepayment(1L, new BigDecimal("50.00")));
    }


    @Test
    void testAddRepaymentWhenLoanIsNotApproved() {
        //given
        Long loanId = 1L;
        Loan loan = Loan.builder().state(LoanStatus.PENDING).id(loanId).build();
        when(loanDAO.findById(loanId)).thenReturn(Optional.of(loan));

        // when and then
        assertThrows(LoanNotApprovedException.class, () -> loanService.addRepayment(1L, new BigDecimal("50.00")));
    }

    @Test
    void testAddRepaymentWhenLoanIsPaid() {
        //given
        Long loanId = 1L;
        Loan loan = Loan.builder().state(LoanStatus.PAID).id(loanId).build();
        when(loanDAO.findById(loanId)).thenReturn(Optional.of(loan));

        // when and then
        assertThrows(NoPendingRepaymentsException.class, () -> loanService.addRepayment(1L, new BigDecimal("50.00")));
    }

    @Test
    void testAddRepaymentWhenLoanIdInvalid() {

        // given
        when(loanDAO.findById(any())).thenReturn(Optional.empty());

        // when and then
        assertThrows(LoanNotFoundException.class, () -> loanService.addRepayment(1L, new BigDecimal("50.00")));
    }
}

