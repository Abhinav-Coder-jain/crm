package com.example.crm.controller;

import com.example.crm.dto.CustomerDTO;
import com.example.crm.entity.Customer;
import com.example.crm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Endpoint for ingesting new customer data
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());

        // Handle optional fields, setting defaults if not provided in DTO
        customer.setTotalSpend(customerDTO.getTotalSpend() != null ? customerDTO.getTotalSpend() : 0.0);
        customer.setVisitCount(customerDTO.getVisitCount() != null ? customerDTO.getVisitCount() : 0);
        customer.setLastVisitDate(customerDTO.getLastVisitDate() != null ? customerDTO.getLastVisitDate() : LocalDate.now());

        Customer savedCustomer = customerService.saveCustomer(customer);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    // Endpoint to get all customers (useful for testing and general view)
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Endpoint to get a single customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Optional: Update customer details (e.g., if totalSpend needs manual adjustment)
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return customerService.getCustomerById(id).map(existingCustomer -> {
            existingCustomer.setName(customerDTO.getName() != null ? customerDTO.getName() : existingCustomer.getName());
            existingCustomer.setEmail(customerDTO.getEmail() != null ? customerDTO.getEmail() : existingCustomer.getEmail());
            existingCustomer.setPhone(customerDTO.getPhone() != null ? customerDTO.getPhone() : existingCustomer.getPhone());
            existingCustomer.setTotalSpend(customerDTO.getTotalSpend() != null ? customerDTO.getTotalSpend() : existingCustomer.getTotalSpend());
            existingCustomer.setVisitCount(customerDTO.getVisitCount() != null ? customerDTO.getVisitCount() : existingCustomer.getVisitCount());
            existingCustomer.setLastVisitDate(customerDTO.getLastVisitDate() != null ? customerDTO.getLastVisitDate() : existingCustomer.getLastVisitDate());

            Customer updatedCustomer = customerService.saveCustomer(existingCustomer);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Optional: Delete a customer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerService.getCustomerById(id).isPresent()) {
            customerService.deleteCustomer(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}