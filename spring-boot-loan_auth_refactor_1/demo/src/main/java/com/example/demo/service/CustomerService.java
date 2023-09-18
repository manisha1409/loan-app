package com.example.demo.service;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Slf4j
public class CustomerService {

    private CustomerDAO customerDAO;

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerDAO.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        log.info("Fetching customer by id: {}", id);
        return customerDAO.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        log.info("Creating customer: {}", customer);
        return customerDAO.save(customer);
    }

    public Optional<Customer> updateCustomer(Long id, Customer updatedCustomer) {
        log.info("Updating customer with id {}: {}", id, updatedCustomer);

        Optional<Customer> existingCustomer = customerDAO.findById(id);
        if (existingCustomer.isPresent()) {
            customerDAO.save(updatedCustomer);
            return Optional.of(updatedCustomer);
        }

        log.warn("Customer with id {} not found for update", id);
        return existingCustomer;
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        customerDAO.deleteById(id);
    }
}
