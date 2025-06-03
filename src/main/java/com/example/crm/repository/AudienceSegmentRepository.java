package com.example.crm.repository;

import com.example.crm.entity.AudienceSegment; // Import your AudienceSegment entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this interface as a Spring Data repository
public interface AudienceSegmentRepository extends JpaRepository<AudienceSegment, Long> {
    // JpaRepository provides common CRUD operations automatically:
    // save(), findById(), findAll(), delete(), etc.

    // You can add custom query methods here if needed,
    // e.g., AudienceSegment findByName(String name);

    void deleteById(Long id);
}