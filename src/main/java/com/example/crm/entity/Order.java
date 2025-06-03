package com.example.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Direct reference to Customer object

    private LocalDateTime orderDate;
    private Double amount; // Corresponds to totalAmount for calculation
    private String itemDetails;

    // Constructors
    public Order() {
    }

    public Order(Customer customer, LocalDateTime orderDate, Double amount, String itemDetails) {
        this.customer = customer;
        this.orderDate = orderDate;
        this.amount = amount;
        this.itemDetails = itemDetails;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getAmount() {
        return amount; // This maps to 'totalAmount' for customer spend
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

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + (customer != null ? customer.getId() : "null") +
                ", orderDate=" + orderDate +
                ", amount=" + amount +
                ", itemDetails='" + itemDetails + '\'' +
                '}';
    }
}