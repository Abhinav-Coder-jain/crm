package com.example.crm.service;

import com.example.crm.entity.Campaign;
import com.example.crm.entity.CampaignStatus; // Added for setting default status
import com.example.crm.entity.CommunicationLog;
import com.example.crm.entity.Customer;
import com.example.crm.repository.CampaignRepository;
import com.example.crm.repository.CommunicationLogRepository;
import com.example.crm.repository.CustomerRepository;
import com.example.crm.util.RuleEngine; // Assuming this is present and works with String rules
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CustomerRepository customerRepository;
    private final CommunicationLogRepository communicationLogRepository;
    private final VendorApiService vendorApiService; // To simulate external message delivery

    @Autowired
    public CampaignService(CampaignRepository campaignRepository,
                           CustomerRepository customerRepository,
                           CommunicationLogRepository communicationLogRepository,
                           VendorApiService vendorApiService) {
        this.campaignRepository = campaignRepository;
        this.customerRepository = customerRepository;
        this.communicationLogRepository = communicationLogRepository;
        this.vendorApiService = vendorApiService;
    }

    /**
     * Creates a new campaign, calculates its audience size, and initiates message delivery.
     * @param campaign The Campaign object (name, description, segmentRules, startDate, endDate)
     * from the request. It should not have id or generated fields set.
     * @param campaignMessageTemplate The template for the message to be sent to customers.
     * @param createdBy The user who initiated the campaign.
     * @return The saved Campaign entity.
     */
    @Transactional
    public Campaign createAndInitiateCampaign(Campaign campaign, String campaignMessageTemplate, String createdBy) {
        // Set audit fields if not already set by @PrePersist or controller
        campaign.setCreatedBy(createdBy);
        if (campaign.getStatus() == null) {
            campaign.setStatus(CampaignStatus.DRAFT); // Default for new campaigns
        }

        // 1. Find target audience based on rules using the RuleEngine
        Set<Customer> audience = RuleEngine.evaluateRules(campaign.getSegmentRules(), customerRepository.findAll());
        campaign.setAudienceSize(audience.size()); // Set the calculated audience size

        // 2. Set sentDate if the campaign is active upon creation
        if (campaign.getStatus() == CampaignStatus.ACTIVE && campaign.getSentDate() == null) {
            campaign.setSentDate(LocalDateTime.now());
        }

        // 3. Save the campaign details
        Campaign savedCampaign = campaignRepository.save(campaign);

        // 4. Initiate message delivery and log communications only if campaign is active
        if (savedCampaign.getStatus() == CampaignStatus.ACTIVE) {
            initiateMessageDelivery(savedCampaign, audience, campaignMessageTemplate);
        }

        return savedCampaign;
    }

    private void initiateMessageDelivery(Campaign campaign, Set<Customer> audience, String campaignMessageTemplate) {
        for (Customer customer : audience) {
            String personalizedMessage = campaignMessageTemplate.replace("{customerName}", customer.getName());

            CommunicationLog logEntry = new CommunicationLog(
                    campaign,
                    customer,
                    personalizedMessage,
                    CommunicationLog.DeliveryStatus.PENDING,
                    LocalDateTime.now()
            );
            communicationLogRepository.save(logEntry);

            // Simulate sending message (this could be async in a real app)
            vendorApiService.sendMessage(logEntry.getId(), personalizedMessage, customer.getPhone());
        }
    }

    public List<Campaign> getAllCampaignsOrderedBySentDateDesc() {
        return campaignRepository.findAllByOrderBySentDateDesc();
    }

    public Optional<Campaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    /**
     * Previews the audience size for given segment rules without creating a campaign.
     * @param segmentRules The rules string to evaluate.
     * @return The size of the audience matching the rules.
     */
    public Integer previewAudienceSize(String segmentRules) {
        Set<Customer> audience = RuleEngine.evaluateRules(segmentRules, customerRepository.findAll());
        return audience.size();
    }

    @Transactional
    public Campaign updateCampaign(Long id, Campaign campaignDetails) {
        return campaignRepository.findById(id).map(campaign -> {
            campaign.setName(campaignDetails.getName());
            campaign.setDescription(campaignDetails.getDescription());
            campaign.setSegmentRules(campaignDetails.getSegmentRules());
            campaign.setStatus(campaignDetails.getStatus());
            campaign.setStartDate(campaignDetails.getStartDate());
            campaign.setEndDate(campaignDetails.getEndDate());

            // If status changes to ACTIVE and sentDate is not set, set it now
            if (campaign.getStatus() == CampaignStatus.ACTIVE && campaign.getSentDate() == null) {
                campaign.setSentDate(LocalDateTime.now());
            }

            // Recalculate audience size if rules change
            if (!campaign.getSegmentRules().equals(campaignDetails.getSegmentRules())) {
                campaign.setAudienceSize(previewAudienceSize(campaignDetails.getSegmentRules()));
            }

            return campaignRepository.save(campaign);
        }).orElseThrow(() -> new IllegalArgumentException("Campaign with ID " + id + " not found."));
    }

    @Transactional
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }

    // Method to update delivery status (called by Delivery Receipt API)
    @Transactional
    public void updateDeliveryStatus(Long communicationLogId, CommunicationLog.DeliveryStatus status) {
        communicationLogRepository.findById(communicationLogId).ifPresent(log -> {
            log.setDeliveryStatus(status);
            log.setDeliveryTimestamp(LocalDateTime.now());
            communicationLogRepository.save(log);
        });
    }

    // Method to get campaign delivery stats (for Campaign History UI)
    public long getSentCountForCampaign(Long campaignId) {
        return communicationLogRepository.countByCampaignIdAndDeliveryStatus(campaignId, CommunicationLog.DeliveryStatus.SENT);
    }

    public long getFailedCountForCampaign(Long campaignId) {
        return communicationLogRepository.countByCampaignIdAndDeliveryStatus(campaignId, CommunicationLog.DeliveryStatus.FAILED);
    }
}