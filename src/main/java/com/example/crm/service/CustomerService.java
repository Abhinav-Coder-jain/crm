package com.example.crm.service;

import com.example.crm.entity.Customer;
import com.example.crm.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer saveCustomer(Customer customer) {
        // Initialize fields if they are null, for new customers
        if (customer.getTotalSpend() == null) {
            customer.setTotalSpend(0.0);
        }
        if (customer.getVisitCount() == null) {
            customer.setVisitCount(0);
        }
        if (customer.getLastVisitDate() == null) {
            customer.setLastVisitDate(LocalDate.now()); // Set current date as default for new customer
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // Example methods for direct querying (complementary to rule engine)
    public List<Customer> getCustomersByTotalSpendGreaterThanEqual(Double spend) {
        return customerRepository.findByTotalSpendGreaterThanEqual(spend);
    }

    public List<Customer> getCustomersByVisitCountLessThanEqual(Integer count) {
        return customerRepository.findByVisitCountLessThanEqual(count);
    }

    public List<Customer> getCustomersInactiveSince(LocalDate date) {
        return customerRepository.findByLastVisitDateBefore(date);
    }
}