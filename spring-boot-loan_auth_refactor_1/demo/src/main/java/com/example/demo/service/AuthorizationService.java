package com.example.demo.service;

import com.example.demo.dao.CustomerDAO;
import com.example.demo.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class AuthorizationService {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public static final String DEFAULT_PASSWORD = "password";

    private  final CustomerDAO customerDAO;

    public AuthorizationService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public boolean isCustomerAuthorized(String authorizationHeader) {
        try {
            log.info("Authorizing customer using Basic authentication.");

            String[] split = extractCredentials(authorizationHeader);
            if (split == null)
                return false;

            String customerId = split[0];
            String password = split[1];
            List<Customer> customers = customerDAO.findAll();
            for (Customer customer : customers) {
                if (customerId.equals((String.valueOf(customer.getId()))) && password.equals(DEFAULT_PASSWORD)) {
                    log.info("Customer authorized successfully.");
                    return true;
                }
            }
            log.warn("Customer authorization failed.");
            return false;
        } catch (Exception exception) {
            log.error("Error while authorizing customer: {}", exception.getMessage());
            return false;
        }
    }

    public boolean isAdminAuthorized(String authorizationHeader) {
        log.info("Authorizing admin using Basic authentication.");

        String[] split = extractCredentials(authorizationHeader);
        if (split == null)
            return false;

        String username = split[0];
        String password = split[1];
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            log.info("Admin authorized successfully.");
            return true;
        } else {
            log.warn("Admin authorization failed.");
            return false;
        }
    }

    public static String[] extractCredentials(String authorizationHeader) {
        try {
            String encodedCredentials = authorizationHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
            return credentials.split(":");
        } catch (Exception exception) {
            log.error("Error while extracting credentials: {}", exception.getMessage());
            return null;
        }
    }



}
