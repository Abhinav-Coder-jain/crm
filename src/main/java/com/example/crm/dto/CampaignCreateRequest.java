package com.example.crm.dto;

import com.example.crm.entity.CampaignStatus; // Import CampaignStatus
import java.time.LocalDateTime;

public class CampaignCreateRequest {
    private String name;
    private String description;
    private String segmentRules;
    private String messageTemplate;
    private CampaignStatus status; // To allow setting initial status
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime sentDate; // For PUT requests, if you want to explicitly set it

    // Constructors (optional, but good practice)
    public CampaignCreateRequest() {}

    public CampaignCreateRequest(String name, String description, String segmentRules, String messageTemplate,
                                 CampaignStatus status, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime sentDate) {
        this.name = name;
        this.description = description;
        this.segmentRules = segmentRules;
        this.messageTemplate = messageTemplate;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sentDate = sentDate;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSegmentRules() {
        return segmentRules;
    }

    public void setSegmentRules(String segmentRules) {
        this.segmentRules = segmentRules;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }
}