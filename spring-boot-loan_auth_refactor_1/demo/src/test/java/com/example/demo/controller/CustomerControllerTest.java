package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomerControllerTest {


    private CustomerService customerService = mock(CustomerService.class);
    private CustomerController customerController = new CustomerController(customerService);


    @Test
    void getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        customers.add(Customer.builder().build());
        when(customerService.getAllCustomers()).thenReturn(customers);

        ResponseEntity<List<Customer>> responseEntity = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(customers, responseEntity.getBody());
    }

    @Test
    void getCustomerById() {
        Long customerId = 1L;
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<Customer> responseEntity = customerController.getCustomerById(customerId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(customer, responseEntity.getBody());
    }

    @Test
    void getCustomerByIdNotFound() {
        Long customerId = 1L;
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());

        ResponseEntity<Customer> responseEntity = customerController.getCustomerById(customerId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void createCustomer() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        when(customerService.createCustomer(customer)).thenReturn(customer);

        ResponseEntity<Customer> responseEntity = customerController.createCustomer(customer);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(customer, responseEntity.getBody());
    }

    @Test
    void updateCustomer() {
        Long customerId = 1L;
        Customer updatedCustomer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        when(customerService.updateCustomer(customerId, updatedCustomer)).thenReturn(Optional.of(updatedCustomer));

        ResponseEntity<Customer> responseEntity = customerController.updateCustomer(customerId, updatedCustomer);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedCustomer, responseEntity.getBody());
    }

    @Test
    void updateCustomerNotFound() {
        Long customerId = 1L;
        Customer updatedCustomer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        when(customerService.updateCustomer(customerId, updatedCustomer)).thenReturn(Optional.empty());

        ResponseEntity<Customer> responseEntity = customerController.updateCustomer(customerId, updatedCustomer);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteCustomer() {
        Long customerId = 1L;

        ResponseEntity<String> responseEntity = customerController.deleteCustomer(customerId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Deleted successfully", responseEntity.getBody());

        verify(customerService, times(1)).deleteCustomer(customerId);
    }
}

