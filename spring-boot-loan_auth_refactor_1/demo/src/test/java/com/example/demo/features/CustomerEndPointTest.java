package com.example.demo.features;

import com.example.demo.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CustomerEndPointTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;
    private String baseUrl;

    @BeforeEach
    public void setup(){
        baseUrl = "http://localhost:" + port + "/v1/customers";
    }

    @Test
    @DisplayName("Create Customer")
    void createCustomer(){
        ResponseEntity<Customer> response = restTemplate.postForEntity(baseUrl, dummyCustomer(),Customer.class);
        String statusCode = response.getStatusCode().toString();
        assertTrue(statusCode.contains("201"));
        cleanUp(response.getBody().getId());
    }

    @Test
    @DisplayName("Get all customers")
    void getAllCustomers(){
        ResponseEntity<Customer[]> response = restTemplate.getForEntity(baseUrl, Customer[].class);
        String statusCode = response.getStatusCode().toString();
        assertTrue(statusCode.contains("200 OK"));
    }

    @Test
    @DisplayName("Get customer by Id")
    void getCustomerById(){

        ResponseEntity<Customer> customerResponseEntity = restTemplate.postForEntity(baseUrl, dummyCustomer(),Customer.class);
        ResponseEntity<Customer> response = restTemplate.getForEntity(baseUrl + "/" + customerResponseEntity.getBody().getId(),
                Customer.class);
        String statusCode = response.getStatusCode().toString();
        assertTrue(statusCode.contains("200 OK"));
        assertEquals(customerResponseEntity.getBody().getId() ,response.getBody().getId());
        cleanUp(response.getBody().getId());
    }

    @Test
    @DisplayName("Update customer by Id")
    void updateCustomerById(){

        ResponseEntity<Customer> customerResponseEntity = restTemplate.postForEntity(baseUrl, dummyCustomer(),Customer.class);
        customerResponseEntity.getBody().setFirstName("Tom");
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + customerResponseEntity.getBody().getId(),
                HttpMethod.PUT, new HttpEntity<>(customerResponseEntity.getBody()), String.class);
        String statusCode = response.getStatusCode().toString();
        assertTrue(statusCode.contains("200 OK"));
    }

    @Test
    @DisplayName("delete Customer")
    void  deleteCustomer(){
        ResponseEntity<Customer> response = restTemplate.postForEntity(baseUrl, dummyCustomer(),Customer.class);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(baseUrl + "/" + response.getBody().getId(),
                HttpMethod.DELETE, new HttpEntity<>(null), String.class);

        String statusCode = deleteResponse.getStatusCode().toString();
        assertTrue(statusCode.contains("200 OK"));
    }

    private ResponseEntity<String> cleanUp(Long id){
        ResponseEntity<String> deleteResponse = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.DELETE, new HttpEntity<>(null), String.class);
        return deleteResponse;
    }

    private Customer dummyCustomer(){
        return Customer.builder()
                .firstName("TestUser")
                .lastName("007")
                .Address("Random Address")
                .email("dummy@email.com")
                .build();
    }
}
