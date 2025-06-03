package com.example.crm.controller;

import com.example.crm.dto.CampaignCreateRequest; // Ensure this DTO is defined
import com.example.crm.entity.Campaign;
import com.example.crm.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // For authenticated user
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private static final Logger logger = LoggerFactory.getLogger(CampaignController.class);

    private final CampaignService campaignService;

    @Autowired
    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    // Endpoint for previewing audience size based on rules
    @PostMapping("/preview-audience")
    public ResponseEntity<Integer> previewAudience(@RequestBody CampaignCreateRequest request) {
        Integer audienceSize = campaignService.previewAudienceSize(request.getSegmentRules());
        return new ResponseEntity<>(audienceSize, HttpStatus.OK);
    }

    // Endpoint for creating and initiating a new campaign
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody CampaignCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdBy = authentication != null ? authentication.getName() : "anonymous";

        // Create a Campaign entity from the DTO, setting fields
        Campaign campaign = new Campaign();
        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription()); // Assuming CampaignCreateRequest has description
        campaign.setSegmentRules(request.getSegmentRules());
        campaign.setStatus(request.getStatus()); // Assuming CampaignCreateRequest has status
        campaign.setStartDate(request.getStartDate()); // Assuming CampaignCreateRequest has startDate
        campaign.setEndDate(request.getEndDate());     // Assuming CampaignCreateRequest has endDate
        // sentDate and audienceSize will be set by the service

        try {
            Campaign newCampaign = campaignService.createAndInitiateCampaign(
                    campaign, // Pass the Campaign entity
                    request.getMessageTemplate(),
                    createdBy
            );
            return new ResponseEntity<>(newCampaign, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating campaign: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating campaign: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all campaigns (for campaign history page)
    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        List<Campaign> campaigns = campaignService.getAllCampaignsOrderedBySentDateDesc();
        return new ResponseEntity<>(campaigns, HttpStatus.OK);
    }

    // Endpoint to get details for a specific campaign (including delivery stats)
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getCampaignDetails(@PathVariable Long id) {
        Optional<Campaign> campaignOpt = campaignService.getCampaignById(id);
        if (campaignOpt.isEmpty()) {
            return new ResponseEntity<>("Campaign not found", HttpStatus.NOT_FOUND);
        }
        Campaign campaign = campaignOpt.get();
        long sent = campaignService.getSentCountForCampaign(id);
        long failed = campaignService.getFailedCountForCampaign(id);

        // Using an inner class for the response DTO
        class CampaignDetailsResponse {
            public Campaign campaign;
            public long sentCount;
            public long failedCount;

            public CampaignDetailsResponse(Campaign campaign, long sentCount, long failedCount) {
                this.campaign = campaign;
                this.sentCount = sentCount;
                this.failedCount = failedCount;
            }
        }
        return new ResponseEntity<>(new CampaignDetailsResponse(campaign, sent, failed), HttpStatus.OK);
    }

    /**
     * PUT /api/campaigns/{id}
     * Updates an existing campaign.
     *
     * @param id The ID of the campaign to update.
     * @param request The updated CampaignCreateRequest DTO.
     * @return The updated Campaign entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable Long id, @RequestBody CampaignCreateRequest request) {
        // Create a Campaign entity from the DTO for update
        Campaign campaignDetails = new Campaign();
        campaignDetails.setName(request.getName());
        campaignDetails.setDescription(request.getDescription());
        campaignDetails.setSegmentRules(request.getSegmentRules());
        campaignDetails.setStatus(request.getStatus());
        campaignDetails.setStartDate(request.getStartDate());
        campaignDetails.setEndDate(request.getEndDate());
        campaignDetails.setSentDate(request.getSentDate()); // Allow updating sentDate if needed

        try {
            Campaign updatedCampaign = campaignService.updateCampaign(id, campaignDetails);
            return new ResponseEntity<>(updatedCampaign, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating campaign {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating campaign {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /api/campaigns/{id}
     * Deletes a campaign by its ID.
     *
     * @param id The ID of the campaign to delete.
     * @return 204 No Content if successful, 404 Not Found if campaign doesn't exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        try {
            campaignService.deleteCampaign(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting campaign {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}