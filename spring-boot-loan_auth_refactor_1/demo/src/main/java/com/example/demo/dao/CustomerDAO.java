package com.example.demo.dao;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDAO extends JpaRepository<Customer, Long> {
}
