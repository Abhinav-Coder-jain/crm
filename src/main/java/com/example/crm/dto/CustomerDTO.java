package com.example.crm.dto;

import java.time.LocalDate;

// This DTO will be used for ingesting new customer data
public class CustomerDTO {

    private String name;
    private String email;
    private String phone;
    private Double totalSpend;
    private LocalDate lastVisitDate;
    private Integer visitCount;

    // Constructors
    public CustomerDTO() {
    }

    public CustomerDTO(String name, String email, String phone, Double totalSpend, LocalDate lastVisitDate, Integer visitCount) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.totalSpend = totalSpend;
        this.lastVisitDate = lastVisitDate;
        this.visitCount = visitCount;
    }

    // Getters and Setters
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
}