package com.example.crm.controller;

import com.example.crm.dto.DeliveryReceiptDTO;
import com.example.crm.entity.CommunicationLog;
import com.example.crm.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery-receipt")
public class DeliveryReceiptController {

    private final CampaignService campaignService;

    @Autowired
    public DeliveryReceiptController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    // This endpoint will be hit by the "vendor API" (simulated by VendorApiService)
    // to update the delivery status of a message.
    @PostMapping
    public ResponseEntity<String> receiveDeliveryReceipt(@RequestBody DeliveryReceiptDTO receipt) {
        if (receipt.getCommunicationLogId() == null || receipt.getStatus() == null) {
            return new ResponseEntity<>("Invalid delivery receipt data.", HttpStatus.BAD_REQUEST);
        }

        try {
            campaignService.updateDeliveryStatus(receipt.getCommunicationLogId(), receipt.getStatus());
            return new ResponseEntity<>("Delivery status updated successfully.", HttpStatus.OK);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error processing delivery receipt for log ID " + receipt.getCommunicationLogId() + ": " + e.getMessage());
            return new ResponseEntity<>("Failed to update delivery status.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}