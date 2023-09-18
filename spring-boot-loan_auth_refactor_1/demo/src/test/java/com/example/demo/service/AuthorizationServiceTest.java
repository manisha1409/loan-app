package com.example.demo.service;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizationServiceTest {

    private CustomerDAO customerDAO = mock(CustomerDAO.class);
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        authorizationService = new AuthorizationService(customerDAO);
        setField(authorizationService, "adminUsername", "Admin");
        setField(authorizationService, "adminPassword", "password");
    }

    @Test
    void testIsCustomerAuthorizedSuccessScenario() {
        // Customer with id 2 and password "password"
        String correctAuthorizationHeader = "Basic MjpwYXNzd29yZA==";
        when(customerDAO.findAll()).thenReturn(List.of(Customer.builder().id(2L).build()));
        assertTrue(authorizationService.isCustomerAuthorized(correctAuthorizationHeader));
    }

    @Test
    void testIsCustomerAuthorizedFailureScenario() {

        // Customer with id 2 and password "password"
        String correctAuthorizationHeader = "Basic MjpwYXNd29yZA==";
        when(customerDAO.findAll()).thenReturn(List.of(Customer.builder().id(2L).build()));
        assertFalse(authorizationService.isCustomerAuthorized(correctAuthorizationHeader));
    }


    @Test
    void testIsAdminAuthorizedSuccessScenario() {
        String authorizationHeader = "Basic QWRtaW46cGFzc3dvcmQ=";  // Admin:password (Base64 encoded)
        assertTrue(authorizationService.isAdminAuthorized(authorizationHeader));
    }

    @Test
    void testIsAdminAuthorizedFailureScenario() {
        String authorizationHeader = "Basic QWRtaW46dXNlcm5hbWU=";  // Admin:username (Base64 encoded)
        assertFalse(authorizationService.isAdminAuthorized(authorizationHeader));
    }

    private void setField(Object target, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
