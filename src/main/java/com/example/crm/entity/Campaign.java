package com.example.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description; // Added back for richer campaign description

    @Lob // Use @Lob for potentially large text fields like segment rules
    @Column(name = "segment_rules", nullable = false , columnDefinition = "TEXT")
    private String segmentRules; // Rules embedded directly as a string

    @Column(name = "audience_size")
    private Integer audienceSize; // Preview audience size calculated at campaign creation/update

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    @Column(nullable = false)
    private CampaignStatus status; // E.g., DRAFT, ACTIVE, COMPLETED, PAUSED

    private LocalDateTime startDate; // Planned start date
    private LocalDateTime endDate; // Planned end date
    private LocalDateTime sentDate; // When the campaign was actually initiated/sent

    private LocalDateTime createdAt;
    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = CampaignStatus.DRAFT; // Default status for new campaigns
        }
    }

    // Constructors
    public Campaign() {
    }

    // Full constructor for convenience
    public Campaign(String name, String description, String segmentRules, Integer audienceSize,
                    CampaignStatus status, LocalDateTime startDate, LocalDateTime endDate,
                    LocalDateTime sentDate, String createdBy) {
        this.name = name;
        this.description = description;
        this.segmentRules = segmentRules;
        this.audienceSize = audienceSize;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sentDate = sentDate;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getAudienceSize() {
        return audienceSize;
    }

    public void setAudienceSize(Integer audienceSize) {
        this.audienceSize = audienceSize;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", segmentRules='" + segmentRules + '\'' +
                ", audienceSize=" + audienceSize +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", sentDate=" + sentDate +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}