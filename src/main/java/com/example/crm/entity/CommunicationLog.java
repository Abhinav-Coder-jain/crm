package com.example.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "communication_logs")
public class CommunicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign; // Link to the campaign this message belongs to

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Link to the customer who received the message

    @Column(length = 2000, nullable = false) // Message content
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus; // PENDING, SENT, FAILED, DELIVERED, READ

    private LocalDateTime sentTimestamp; // When the message was initiated by CRM
    private LocalDateTime deliveryTimestamp; // When the vendor confirmed delivery/failure

    // Constructors
    public CommunicationLog() {
    }

    public CommunicationLog(Campaign campaign, Customer customer, String messageContent, DeliveryStatus deliveryStatus, LocalDateTime sentTimestamp) {
        this.campaign = campaign;
        this.customer = customer;
        this.messageContent = messageContent;
        this.deliveryStatus = deliveryStatus;
        this.sentTimestamp = sentTimestamp;
    }

    // Enum for Delivery Status
    public enum DeliveryStatus {
        PENDING,
        SENT,
        FAILED,
        DELIVERED, // Could be a separate status if vendor provides it
        READ       // Could be a separate status if vendor provides it
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDateTime getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(LocalDateTime sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public LocalDateTime getDeliveryTimestamp() {
        return deliveryTimestamp;
    }

    public void setDeliveryTimestamp(LocalDateTime deliveryTimestamp) {
        this.deliveryTimestamp = deliveryTimestamp;
    }

    @Override
    public String toString() {
        return "CommunicationLog{" +
                "id=" + id +
                ", campaignId=" + (campaign != null ? campaign.getId() : "null") +
                ", customerId=" + (customer != null ? customer.getId() : "null") +
                ", deliveryStatus=" + deliveryStatus +
                ", sentTimestamp=" + sentTimestamp +
                ", deliveryTimestamp=" + deliveryTimestamp +
                '}';
    }
}