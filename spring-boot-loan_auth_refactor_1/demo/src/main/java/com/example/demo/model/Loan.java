package com.example.demo.model;

import com.example.demo.enums.LoanStatus;
import com.example.demo.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal amount;

    private LoanType loanType;

    private int term;

    private LocalDate requestDate;

    private LoanStatus state;

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", customer=" + customer +
                ", amount=" + amount +
                ", loanType=" + loanType +
                ", term=" + term +
                ", requestDate=" + requestDate +
                ", state=" + state +
                ", scheduledRepayments=" + scheduledRepayments +
                '}';
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "loan_id")
    private List<ScheduledRepayment> scheduledRepayments;

}
