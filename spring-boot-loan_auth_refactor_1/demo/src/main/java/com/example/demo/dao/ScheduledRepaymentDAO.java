package com.example.demo.dao;

import com.example.demo.model.ScheduledRepayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledRepaymentDAO extends JpaRepository<ScheduledRepayment, Long> {
}
