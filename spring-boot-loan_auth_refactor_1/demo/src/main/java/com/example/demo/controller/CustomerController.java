package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.example.demo.service.AuthorizationService.DEFAULT_PASSWORD;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);

        return customer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Optional<Customer> updatedCustomer = customerService.updateCustomer(id, customer);
        return updatedCustomer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    //Utility API for testing
    @GetMapping("/password/{id}")
    public String getCustomerPassword(@PathVariable Long id) {
        String password = String.valueOf(id) + ":" + DEFAULT_PASSWORD;
        byte[] bytesEncoded = Base64.getEncoder().encode(password.getBytes(StandardCharsets.UTF_8));
        return new String(bytesEncoded);
    }
}


