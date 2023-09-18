package com.example.demo.dao;

import com.example.demo.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanDAO extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomer_Id(Long customerId);
}
