package com.example.demo.model;

import com.example.demo.enums.RepaymentState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_repayments")
public class ScheduledRepayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id")
    private Long loanId;

    private BigDecimal totalRepaymentAmount;

    private BigDecimal paymentPending;

    private LocalDate repaymentDate;

    private RepaymentState state;

    @Override
    public String toString() {
        return "ScheduledRepayment{" +
                "id=" + id +
                ", loanId=" + loanId +
                ", totalRepaymentAmount=" + totalRepaymentAmount +
                ", paymentPending=" + paymentPending +
                ", repaymentDate=" + repaymentDate +
                ", state=" + state +
                '}';
    }
}

