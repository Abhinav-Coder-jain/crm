package com.example.crm.repository;

import com.example.crm.entity.Campaign;
import com.example.crm.entity.CampaignStatus; // Make sure this is imported
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    // For the campaign history page, we'll want to list campaigns, most recent first.
    List<Campaign> findAllByOrderBySentDateDesc();
    List<Campaign> findByCreatedBy(String createdBy);
    List<Campaign> findByStatus(CampaignStatus status); // Now that Campaign has a status field
}