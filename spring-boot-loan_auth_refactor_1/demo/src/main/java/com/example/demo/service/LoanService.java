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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanService {

    private final LoanDAO loanDAO;
    private final ScheduledRepaymentDAO scheduledRepaymentDAO;
    private final CustomerDAO customerDAO;
    private final LoanEligibilityService loanEligibilityService;

    @Autowired
    public LoanService(LoanDAO loanDAO,
                       ScheduledRepaymentDAO scheduledRepaymentDAO,
                       CustomerDAO customerDAO,
                       LoanEligibilityService loanEligibilityService) {
        this.loanDAO = loanDAO;
        this.scheduledRepaymentDAO = scheduledRepaymentDAO;
        this.customerDAO = customerDAO;
        this.loanEligibilityService = loanEligibilityService;
    }

    public Loan createLoanForCustomer(Long customerId, BigDecimal amount, int term, LoanType loanType, LocalDate requestDate) {

        log.info("Creating loan for customer with ID: {}, amount: {}, term: {}, loan type: {}, request date: {}",
                customerId, amount, term, loanType, requestDate);

        Optional<Customer> optionalCustomer = customerDAO.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            return createLoan(customer, amount, term, loanType, requestDate);
        }
        log.warn("Customer Id is not valid");
        return null;
    }

    public Loan approveLoan(Long loanId) {

        log.info("Approving loan with ID: {}", loanId);
        Optional<Loan> optionalLoan = loanDAO.findById(loanId);

        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            validateIfCustomerIsEligibleForLoan(loan.getCustomer());
            loan.setState(LoanStatus.APPROVED);
            return loanDAO.save(loan);
        } else {
            throw new LoanNotFoundException();
        }
    }

    public Loan addRepayment(Long loanId, BigDecimal repaymentAmount) {
        log.info("Adding repayment for loan with ID: {}, repayment amount: {}", loanId, repaymentAmount);
        Optional<Loan> optionalLoan = loanDAO.findById(loanId);
        validateRepayments(optionalLoan);
        Loan loan = optionalLoan.get();

        ScheduledRepayment pendingRepayment = findFirstPendingRepayment(loan);
        if (repaymentAmount.compareTo(pendingRepayment.getPaymentPending()) < 0)
            throw new RepaymentAmountNotSufficientException();

        while (repaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            ScheduledRepayment nextPendingRepayment = findFirstPendingRepayment(loan);

            if (nextPendingRepayment == null)
                break;
            BigDecimal paymentPending = nextPendingRepayment.getPaymentPending();

            if (repaymentAmount.compareTo(paymentPending) >= 0) {
                repaymentAmount = repaymentAmount.subtract(paymentPending);
                nextPendingRepayment.setPaymentPending(BigDecimal.ZERO);
                nextPendingRepayment.setState(RepaymentState.PAID);
            } else {
                nextPendingRepayment.setPaymentPending(paymentPending.subtract(repaymentAmount));
                repaymentAmount = BigDecimal.ZERO;
            }
            scheduledRepaymentDAO.save(nextPendingRepayment);
        }

        boolean allRepaymentsPaid = checkIfAllRepaymentsPaid(loan);
        if (allRepaymentsPaid) {
            loan.setState(LoanStatus.PAID);
            loanDAO.save(loan);
        }

        return loan;

    }

    private void validateRepayments(Optional<Loan> optionalLoan) {
        if (optionalLoan.isEmpty())
            throw new LoanNotFoundException();

        Loan loan = optionalLoan.get();

        if (loan.getState().equals(LoanStatus.PENDING))
            throw new LoanNotApprovedException();

        if (loan.getState().equals(LoanStatus.PAID))
            throw new NoPendingRepaymentsException();
    }


    public List<Loan> fetchLoansByCustomerId(Long customerId) {
        log.info("Fetching loans for customer with ID: {}", customerId);
        return loanDAO.findByCustomer_Id(customerId);
    }



    private Loan createLoan(Customer customer, BigDecimal amount, int term, LoanType loanType, LocalDate requestDate) {
        Loan loanDto = Loan.builder()
                .customer(customer)
                .amount(amount)
                .term(term)
                .loanType(loanType)
                .requestDate(requestDate)
                .state(LoanStatus.PENDING)
                .build();

        List<ScheduledRepayment> scheduledRepayments = calculateScheduledRepayments(amount, term, requestDate, loanDto);
        loanDto.setScheduledRepayments(scheduledRepayments);
        return loanDAO.save(loanDto);
    }

    private List<ScheduledRepayment> calculateScheduledRepayments(BigDecimal amount, int term, LocalDate requestDate, Loan loan) {
        log.info("Calculating scheduled repayments for loan with ID: {}, amount: {}, term: {}, request date: {}",
                loan.getId(), amount, term, requestDate);
        List<ScheduledRepayment> scheduledRepayments = new ArrayList<>();
        BigDecimal repaymentAmount = calculateLoanAmount(loan.getAmount(), term);
        LocalDate repaymentDate = requestDate;
        for (int i = 0; i < term; i++) {
            ScheduledRepayment repayment = ScheduledRepayment.builder()
                    .loanId(loan.getId())
                    .totalRepaymentAmount(repaymentAmount)
                    .paymentPending(repaymentAmount)
                    .repaymentDate(repaymentDate)
                    .state(RepaymentState.PENDING).build();

            scheduledRepayments.add(repayment);
            repaymentDate = incrementByOneWeek(repaymentDate);
        }

        // Adjusting the last repayment amount to ensure total matches the loan amount
        scheduledRepayments.get(term - 1).setTotalRepaymentAmount(amount.subtract(repaymentAmount.multiply(BigDecimal.valueOf(term - 1))));
        scheduledRepayments.get(term - 1).setPaymentPending(amount.subtract(repaymentAmount.multiply(BigDecimal.valueOf(term - 1))));
        return scheduledRepayments;
    }

    private BigDecimal calculateLoanAmount(BigDecimal principal, int loanTerm) {
        return principal.divide(new BigDecimal(loanTerm), 2, RoundingMode.HALF_UP);
    }

    private LocalDate incrementByOneWeek(LocalDate date) {
        return date.plusWeeks(1);
    }

    private void validateIfCustomerIsEligibleForLoan(Customer customer) {
        if (!loanEligibilityService.checkLoanEligibility(customer))
            throw new CustomerNotEligibleException();
    }


    private ScheduledRepayment findFirstPendingRepayment(Loan loan) {
        for (ScheduledRepayment repayment : loan.getScheduledRepayments()) {
            if (repayment.getState().equals(RepaymentState.PENDING)) {
                return repayment;
            }
        }
        return null;
    }

    private boolean checkIfAllRepaymentsPaid(Loan loan) {
        for (ScheduledRepayment repayment : loan.getScheduledRepayments()) {
            if (repayment.getState().equals(RepaymentState.PENDING)) {
                return false;
            }
        }
        return true;
    }
}
