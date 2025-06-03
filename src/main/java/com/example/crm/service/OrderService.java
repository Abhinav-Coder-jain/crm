package com.example.crm.service;

import com.example.crm.entity.Customer;
import com.example.crm.entity.Order;
import com.example.crm.repository.CustomerRepository;
import com.example.crm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Order saveOrder(Order order) {
        // Ensure the customer associated with the order exists
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Order must be associated with an existing customer with a valid ID.");
        }

        Optional<Customer> customerOpt = customerRepository.findById(order.getCustomer().getId());
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer with ID " + order.getCustomer().getId() + " not found. Cannot create order.");
        }

        Customer customer = customerOpt.get();

        // Update customer's total spend and visit count
        customer.setTotalSpend(customer.getTotalSpend() != null ? customer.getTotalSpend() + order.getAmount() : order.getAmount());
        customer.setVisitCount(customer.getVisitCount() != null ? customer.getVisitCount() + 1 : 1);
        customer.setLastVisitDate(order.getOrderDate() != null ? order.getOrderDate().toLocalDate() : LocalDate.now()); // Update last visit to order date or current date

        customerRepository.save(customer); // Save updated customer details

        // Set order date if not provided in the incoming order object
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        // You might need to add a method to your OrderRepository:
        // List<Order> findByCustomerId(Long customerId);
        // Or if using @ManyToOne, you might use: findByCustomer_Id(Long customerId);
        // Assuming findByCustomerId is already defined in OrderRepository or will be.
        return orderRepository.findByCustomerId(customerId); // Use findByCustomer_Id for ManyToOne
    }
}