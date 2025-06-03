package com.example.crm.dto;

import java.time.LocalDateTime;

// This DTO will be used for ingesting new order data
public class OrderDTO {

    private Long customerId; // The ID of the customer associated with this order
    private LocalDateTime orderDate;
    private Double amount;
    private String itemDetails;

    // Constructors
    public OrderDTO() {
    }

    public OrderDTO(Long customerId, LocalDateTime orderDate, Double amount, String itemDetails) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.amount = amount;
        this.itemDetails = itemDetails;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }
}