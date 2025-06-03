package com.example.crm.repository;

import com.example.crm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom query to find orders by customer ID
    List<Order> findByCustomerId(Long customerId);
}