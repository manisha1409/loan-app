package com.example.demo.model;

import com.example.demo.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", Address='" + Address + '\'' +
                ", email='" + email + '\'' +
                ", creditScore=" + creditScore +
                ", employmentStatus=" + employmentStatus +
                '}';
    }

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address")
    private String Address;

    @Column(name = "email")
    private String email;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;


}
