package com.example.crm.dto;

import com.example.crm.entity.CommunicationLog;

// This DTO will be used by the simulated vendor API to send delivery receipts back to your CRM
public class DeliveryReceiptDTO {

    private Long communicationLogId;
    private CommunicationLog.DeliveryStatus status; // SENT or FAILED

    // Constructors
    public DeliveryReceiptDTO() {
    }

    public DeliveryReceiptDTO(Long communicationLogId, CommunicationLog.DeliveryStatus status) {
        this.communicationLogId = communicationLogId;
        this.status = status;
    }

    // Getters and Setters
    public Long getCommunicationLogId() {
        return communicationLogId;
    }

    public void setCommunicationLogId(Long communicationLogId) {
        this.communicationLogId = communicationLogId;
    }

    public CommunicationLog.DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicationLog.DeliveryStatus status) {
        this.status = status;
    }
}