package com.example.demo.service;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerDAO customerDAO = mock(CustomerDAO.class);
    private CustomerService customerService = new CustomerService(customerDAO);


    @Test
    void testGetAllCustomers() {
        // given
        List<Customer> customers = new ArrayList<>();
        customers.add(Customer.builder().id(1L).firstName("Tom").lastName("Ray").build());
        customers.add(Customer.builder().id(1L).firstName("Jane").lastName("Doe").build());
        when(customerDAO.findAll()).thenReturn(customers);

        //when
        List<Customer> result = customerService.getAllCustomers();

        // then
        assertEquals(2, result.size());
        assertEquals("Tom", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void testGetCustomerById() {
        // given
        long id = 1L;
        Customer customer = Customer.builder().id(1L).firstName("Jane").lastName("Doe").build();
        when(customerDAO.findById(id)).thenReturn(Optional.of(customer));

        //when
        Optional<Customer> result = customerService.getCustomerById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
    }

    @Test
    void testGetCustomerByIdEmptyScenario() {
        // given
        long id = 1L;
        when(customerDAO.findById(id)).thenReturn(Optional.empty());

        //when
        Optional<Customer> result = customerService.getCustomerById(id);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateCustomer() {
        // given
        Customer customer = Customer.builder().id(1L).firstName("Jane").lastName("Doe").build();
        when(customerDAO.save(any())).thenReturn(customer);

        //when
        Customer result = customerService.createCustomer(customer);

        // then
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testUpdateCustomer() {
        // given
        Customer customer = Customer.builder().id(1L).firstName("Jane").lastName("Doe").build();
        when(customerDAO.findById(any())).thenReturn(Optional.of(customer));
        when(customerDAO.save(any())).thenReturn(customer);

        //when
        Optional<Customer> result = customerService.updateCustomer(1L,customer);

        // then
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    void testUpdateCustomerIfCustomerIsNotPresent() {
        // given
        Customer customer = Customer.builder().id(1L).firstName("Jane").lastName("Doe").build();
        when(customerDAO.findById(any())).thenReturn(Optional.empty());

        //when
        Optional<Customer> result = customerService.updateCustomer(1L,customer);

        // then
        assertEquals(false, result.isPresent());
    }

    @Test
    void testDeleteCustomer() {

        //when
        customerService.deleteCustomer(1L);

        // then
        verify(customerDAO, times(1)).deleteById(1L);
    }


}

