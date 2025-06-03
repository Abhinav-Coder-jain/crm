package com.example.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDate; // For lastVisitDate

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private Double totalSpend;
    private LocalDate lastVisitDate; // To track last interaction/purchase
    private Integer visitCount; // Number of times customer visited/purchased

    // Constructors
    public Customer() {
    }

    public Customer(String name, String email, String phone, Double totalSpend, LocalDate lastVisitDate, Integer visitCount) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.totalSpend = totalSpend;
        this.lastVisitDate = lastVisitDate;
        this.visitCount = visitCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(Double totalSpend) {
        this.totalSpend = totalSpend;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", totalSpend=" + totalSpend +
                ", lastVisitDate=" + lastVisitDate +
                ", visitCount=" + visitCount +
                '}';
    }
}