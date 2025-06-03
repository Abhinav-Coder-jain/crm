package com.example.crm.controller;

import com.example.crm.dto.OrderDTO;
import com.example.crm.entity.Customer;
import com.example.crm.entity.Order;
import com.example.crm.service.CustomerService;
import com.example.crm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService; // To fetch customer for linking

    @Autowired
    public OrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    // Endpoint for ingesting new order data
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        // Validate customer ID
        Optional<Customer> customerOpt = customerService.getCustomerById(orderDTO.getCustomerId());
        if (customerOpt.isEmpty()) {
            return new ResponseEntity<>("Customer with ID " + orderDTO.getCustomerId() + " not found.", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setCustomer(customerOpt.get()); // Link to the existing customer
        order.setOrderDate(orderDTO.getOrderDate() != null ? orderDTO.getOrderDate() : LocalDateTime.now());
        order.setAmount(orderDTO.getAmount());
        order.setItemDetails(orderDTO.getItemDetails());

        try {
            Order savedOrder = orderService.saveOrder(order);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while saving the order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Endpoint to get orders by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}