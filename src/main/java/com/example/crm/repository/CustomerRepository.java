package com.example.crm.repository;

import com.example.crm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // You can add custom query methods here if needed
    // For example, to find customers by email:
    // Optional<Customer> findByEmail(String email);

    // Example for audience segmentation:
    List<Customer> findByTotalSpendGreaterThanEqual(Double totalSpend);
    List<Customer> findByVisitCountLessThanEqual(Integer visitCount);
    List<Customer> findByLastVisitDateBefore(LocalDate date);
    List<Customer> findByLastVisitDateAfter(LocalDate date);
    List<Customer> findByLastVisitDateBeforeAndTotalSpendGreaterThanEqual(LocalDate date, Double totalSpend);
    // You will build more complex queries using a combination or through custom queries later.
}