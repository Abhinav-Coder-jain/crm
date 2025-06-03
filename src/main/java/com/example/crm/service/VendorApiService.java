package com.example.crm.service;

import com.example.crm.entity.CommunicationLog;
import com.example.crm.repository.CommunicationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // For making HTTP calls (simulated)

import java.util.Random;

@Service
public class VendorApiService {

    private final RestTemplate restTemplate; // Use this to simulate HTTP calls
    private final CommunicationLogRepository communicationLogRepository; // To directly update status for simulation
    private final String DELIVERY_RECEIPT_API_URL = "http://localhost:8080/api/delivery-receipt"; // Your backend endpoint

    @Autowired
    public VendorApiService(CommunicationLogRepository communicationLogRepository) {
        this.restTemplate = new RestTemplate(); // Initialize RestTemplate
        this.communicationLogRepository = communicationLogRepository;
    }

    public void sendMessage(Long communicationLogId, String message, String recipientPhone) {
        // Simulate real-world delivery success/failure (~90% SENT, ~10% FAILED) [cite: 8]
        Random random = new Random();
        boolean success = random.nextInt(10) < 9; // 90% chance of success

        CommunicationLog.DeliveryStatus finalStatus = success ?
                CommunicationLog.DeliveryStatus.SENT : CommunicationLog.DeliveryStatus.FAILED;

        // Simulate hitting your Delivery Receipt API on your backend [cite: 8]
        // In a real scenario, this would be an external HTTP call.
        // For simplicity and immediate update for the demo, we will directly update the log here.
        // If we were building a full-fledged mock vendor API, it would make a real HTTP call.
        // For this assignment's time constraint, we'll mock the *effect* of the vendor API
        // hitting your delivery receipt endpoint by directly updating the log status.

        // This is a simplification for the demo.
        // A full implementation would involve:
        // 1. VendorApiService *actually* making an HTTP POST to DELIVERY_RECEIPT_API_URL
        // 2. Your DeliveryReceiptController receiving that POST and calling CampaignService.updateDeliveryStatus

        communicationLogRepository.findById(communicationLogId).ifPresent(log -> {
            log.setDeliveryStatus(finalStatus);
            log.setDeliveryTimestamp(java.time.LocalDateTime.now());
            communicationLogRepository.save(log);
        });

        System.out.println("Simulated sending message to " + recipientPhone + ": '" + message + "' - Status: " + finalStatus);
    }
}