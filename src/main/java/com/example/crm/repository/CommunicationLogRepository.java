package com.example.crm.repository;

import com.example.crm.entity.CommunicationLog;
import com.example.crm.entity.CommunicationLog.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationLogRepository extends JpaRepository<CommunicationLog, Long> {
    // Find all logs for a specific campaign
    List<CommunicationLog> findByCampaignId(Long campaignId);

    // Count delivery statuses for a campaign
    long countByCampaignIdAndDeliveryStatus(Long campaignId, DeliveryStatus status);
}