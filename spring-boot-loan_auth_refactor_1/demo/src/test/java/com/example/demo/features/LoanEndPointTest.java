package com.example.demo.features;

import com.example.demo.enums.EmploymentStatus;
import com.example.demo.enums.LoanStatus;
import com.example.demo.enums.RepaymentState;
import com.example.demo.model.Customer;
import com.example.demo.model.Loan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
public class LoanEndPointTest {
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    public final String DEFAULT_PASSWORD = "password";
    @LocalServerPort
    private int port;
    private String baseUrl;
    private String baseCustomerUrl;

    @BeforeEach
    public void setup(){
        baseUrl = "http://localhost:" + port + "/v1/loans";
        baseCustomerUrl = "http://localhost:" + port + "/v1/customers";
    }

    @Test
    @DisplayName("Create Loan")
    void createLoan(){
        Customer customer = createCustomer();
        String auth = customer.getId().toString()+ ":"+ DEFAULT_PASSWORD;
        headers.add("Authorization", "Basic "+ encryptString(auth));

        ResponseEntity<Loan> response = restTemplate.postForEntity(baseUrl + "/" + customer.getId() +
                "?amount=" + 1000 +"&term=" + 5 +"&loanType="+ "HOME",new HttpEntity<>(null, headers),Loan.class);
        String statusCode = response.getStatusCode().toString();
        Loan resultLoan = response.getBody();

        log.info("Created Loan : {} ",response.getBody());
        assertTrue(statusCode.contains("200 OK"));
        assertEquals(LoanStatus.PENDING, resultLoan.getState());
        assertEquals(5, resultLoan.getScheduledRepayments().size());
        cleanUpCustomer(customer.getId());
    }

    @Test
    @DisplayName("approve Loan")
    void approveLoan(){
        Customer customer = createCustomer();
        String customerAuth = customer.getId().toString()+ ":"+ DEFAULT_PASSWORD;
        headers.add("Authorization", "Basic "+ encryptString(customerAuth));

        ResponseEntity<Loan> customerResponse = restTemplate.postForEntity(baseUrl + "/" + customer.getId() +
                "?amount=" + 1000 +"&term=" + 5 +"&loanType="+ "HOME",new HttpEntity<>(null, headers),Loan.class);

        Long loanId = customerResponse.getBody().getId();


        String auth = "admin:password";
        headers.remove("Authorization");
        headers.add("Authorization", "Basic " + encryptString(auth));
        ResponseEntity<Loan> response = restTemplate.exchange(baseUrl + "/approve/" + loanId , HttpMethod.PUT,
                new HttpEntity<>(null, headers),Loan.class);
        String statusCode = response.getStatusCode().toString();
        Loan resultLoan = response.getBody();


        log.info("Approved Loan : {} ",response.getBody());
        assertTrue(statusCode.contains("200 OK"));
        assertEquals(LoanStatus.APPROVED, resultLoan.getState());
    }


    @Test
    @DisplayName("Get  Loan by customerId")
    void getLoanByCustomerId(){
        Customer customer = createCustomer();
        String auth = customer.getId().toString()+ ":"+ DEFAULT_PASSWORD;
        headers.add("Authorization", "Basic "+ encryptString(auth));

        ResponseEntity<Loan> loanResponseEntity = restTemplate.postForEntity(baseUrl + "/" + customer.getId() +
                "?amount=" + 1000 +"&term=" + 5 +"&loanType="+ "HOME",new HttpEntity<>(null, headers),Loan.class);

        ResponseEntity<Loan[]> response =  restTemplate.exchange(baseUrl + "/" + customer.getId() , HttpMethod.GET,
                new HttpEntity<>(null, headers),Loan[].class);
        String statusCode = response.getStatusCode().toString();

        log.info("Loan : {} ",response.getBody());
        assertTrue(statusCode.contains("200 OK"));
        assertEquals(loanResponseEntity.getBody().getId(),response.getBody()[0].getId());
        assertEquals(1, response.getBody().length);
    }

    @Test
    @DisplayName("pay Loan")
    void payLoan(){
        Customer customer = createCustomer();
        String customerAuth = customer.getId().toString()+ ":"+ DEFAULT_PASSWORD;
        headers.add("Authorization", "Basic "+ encryptString(customerAuth));

        ResponseEntity<Loan> customerResponse = restTemplate.postForEntity(baseUrl + "/" + customer.getId() +
                "?amount=" + 1000 +"&term=" + 5 +"&loanType="+ "HOME",new HttpEntity<>(null, headers),Loan.class);

        Long loanId = customerResponse.getBody().getId();


        String auth = "admin:password";
        headers.remove("Authorization");
        headers.add("Authorization", "Basic " + encryptString(auth));
        ResponseEntity<Loan> response = restTemplate.exchange(baseUrl + "/approve/" + loanId , HttpMethod.PUT,
                new HttpEntity<>(null, headers),Loan.class);


        headers.remove("Authorization");
        headers.add("Authorization", "Basic " + encryptString(customerAuth));
        ResponseEntity<Loan> paidLoan = restTemplate.postForEntity(baseUrl + "/repayments/" + response.getBody().getId() +
                "?repaymentAmount=" + 400 ,new HttpEntity<>(null, headers),Loan.class);


        log.info("Loan afer payment: {} ",paidLoan.getBody());
        assertTrue(paidLoan.getStatusCode().toString().contains("200 OK"));
        assertEquals(RepaymentState.PAID, paidLoan.getBody().getScheduledRepayments().get(0).getState());
    }

    private Customer createCustomer(){
        ResponseEntity<Customer> response = restTemplate.postForEntity(baseCustomerUrl, dummyCustomer(),Customer.class);
        return  response.getBody();
    }

    private ResponseEntity<String> cleanUpCustomer(Long id){
        ResponseEntity<String> deleteResponse = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.DELETE, new HttpEntity<>(null), String.class);
        return deleteResponse;
    }
    private Customer dummyCustomer(){
        return Customer.builder()
                .firstName("TestUser")
                .lastName("007")
                .creditScore(700)
                .employmentStatus(EmploymentStatus.FULL_TIME)
                .Address("Random Address")
                .email("dummy@email.com")
                .build();
    }

    public String encryptString(String string) {
        byte[] bytesEncoded = Base64.getEncoder().encode(string.getBytes(StandardCharsets.UTF_8));
        return new String(bytesEncoded);
    }

}
