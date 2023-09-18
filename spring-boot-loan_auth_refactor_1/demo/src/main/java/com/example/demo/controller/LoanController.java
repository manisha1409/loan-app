package com.example.demo.controller;

import com.example.demo.enums.LoanType;
import com.example.demo.service.AuthorizationService;
import com.example.demo.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.demo.service.AuthorizationService.extractCredentials;

@RestController
@RequestMapping("/v1/loans")
public class LoanController {

    private final LoanService loanService;
    private final AuthorizationService authorizationService;
    public LoanController(LoanService loanService, AuthorizationService authorizationService) {
        this.loanService = loanService;
        this.authorizationService = authorizationService;
    }
    @PostMapping("/{customerId}")
    public ResponseEntity<Object> createLoan(@PathVariable Long customerId,
                                             @RequestParam BigDecimal amount,
                                             @RequestParam int term,
                                             @RequestParam LoanType loanType,
                                             @RequestHeader("Authorization") String authorization) {

        if (!authorizationService.isCustomerAuthorized(authorization))
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.ok(loanService.createLoanForCustomer(customerId, amount, term, loanType, LocalDate.now()));
    }


    @PutMapping("/approve/{loanId}")
    public ResponseEntity<Object> approveLoan(@PathVariable Long loanId,
                                              @RequestHeader("Authorization") String authorization) {

        if (!authorizationService.isAdminAuthorized(authorization))
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.ok(loanService.approveLoan(loanId));
    }

    @PostMapping("/repayments/{loanId}")
    public ResponseEntity<Object> addRepayment(@PathVariable Long loanId,
                                               @RequestParam BigDecimal repaymentAmount,
                                               @RequestHeader("Authorization") String authorization) {
        try {

            if (!authorizationService.isCustomerAuthorized(authorization))
                return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.ok(loanService.addRepayment(loanId, repaymentAmount));
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Object> loanByCustomerId(@PathVariable Long customerId,
                                                   @RequestHeader("Authorization") String authorization) {

        if (!authorizationService.isCustomerAuthorized(authorization))
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        String loggedInCustomerId = extractCredentials(authorization)[0];
        if (!loggedInCustomerId.equals(String.valueOf(customerId)))
            return new ResponseEntity<>("Customer can view only their loans",HttpStatus.UNAUTHORIZED);
        return ResponseEntity.ok(loanService.fetchLoansByCustomerId(customerId));
    }
}
