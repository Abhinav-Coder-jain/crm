package com.example.crm.controller;

import com.example.crm.entity.AudienceSegment;
import com.example.crm.entity.segmentation.RuleGroup; // Import RuleGroup for preview endpoint
import com.example.crm.service.AudienceSegmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // Needed for handling RuleGroup JSON
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/audience-segments") // Base path for all endpoints in this controller
public class AudienceSegmentController {

    private static final Logger logger = LoggerFactory.getLogger(AudienceSegmentController.class);

    private final AudienceSegmentService audienceSegmentService;
    private final ObjectMapper objectMapper; // To convert RuleGroup to JSON for saving

    @Autowired
    public AudienceSegmentController(AudienceSegmentService audienceSegmentService, ObjectMapper objectMapper) {
        this.audienceSegmentService = audienceSegmentService;
        this.objectMapper = objectMapper;
    }

    /**
     * POST /api/audience-segments
     * Creates a new audience segment.
     * The client will send the RuleGroup object, which needs to be serialized to JSON.
     * @param segment The AudienceSegment object containing name, description, and ruleGroup.
     * @return The created AudienceSegment with its ID.
     */
    @PostMapping
    public ResponseEntity<AudienceSegment> createAudienceSegment(@RequestBody AudienceSegment segment) {
        try {
            // Convert the RuleGroup object from the request body into a JSON string
            // and set it to ruleLogicJson field of AudienceSegment entity.
            // Assuming the client sends a segment with ruleLogicJson already set.
            // If client sends RuleGroup object directly, you'd need a DTO.
            // For now, we assume the RuleGroup is already JSON string in segment.ruleLogicJson
            // Or you could make the API accept a DTO with RuleGroup and then serialize it.

            // To support both scenarios (RuleGroup directly or ruleLogicJson string):
            // For this example, let's assume the frontend sends the rule logic as a JSON string.
            // If the frontend were to send a RuleGroup object, you'd need a DTO and convert it.

            // If you want to accept a RuleGroup object in the DTO, the process would be:
            // public ResponseEntity<AudienceSegment> createAudienceSegment(@RequestBody AudienceSegmentRequestDTO dto) {
            //     AudienceSegment segment = new AudienceSegment();
            //     segment.setName(dto.getName());
            //     segment.setDescription(dto.getDescription());
            //     segment.setRuleLogicJson(objectMapper.writeValueAsString(dto.getRuleGroup()));
            //     // ... other fields
            // }

            AudienceSegment createdSegment = audienceSegmentService.saveAudienceSegment(segment);

            // Optionally, calculate and set preview size upon creation
            try {
                int previewSize = audienceSegmentService.previewAudienceSize(createdSegment);
                createdSegment.setPreviewAudienceSize(previewSize);
                // Save again if you want the preview size to be persisted immediately
                // audienceSegmentService.saveAudienceSegment(createdSegment);
            } catch (JsonProcessingException e) {
                logger.error("Error calculating preview size for new segment: {}", createdSegment.getName(), e);
                // Continue without preview size or return specific error
            }

            return new ResponseEntity<>(createdSegment, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating audience segment: {}", segment.getName(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /api/audience-segments
     * Retrieves all audience segments.
     * @return A list of all AudienceSegment entities.
     */
    @GetMapping
    public ResponseEntity<List<AudienceSegment>> getAllAudienceSegments() {
        List<AudienceSegment> segments = audienceSegmentService.getAllAudienceSegments();
        return new ResponseEntity<>(segments, HttpStatus.OK);
    }

    /**
     * GET /api/audience-segments/{id}
     * Retrieves an audience segment by its ID.
     * @param id The ID of the audience segment.
     * @return The AudienceSegment if found, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AudienceSegment> getAudienceSegmentById(@PathVariable Long id) {
        Optional<AudienceSegment> segment = audienceSegmentService.getAudienceSegmentById(id);
        return segment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST /api/audience-segments/preview
     * Previews the audience size for a given set of rule logic.
     * The client sends the RuleGroup object, which will be serialized to JSON within the AudienceSegment for evaluation.
     * @param ruleGroup The RuleGroup object representing the segmentation criteria.
     * @return The calculated audience size.
     */
    @PostMapping("/preview")
    public ResponseEntity<Integer> previewAudienceSize(@RequestBody RuleGroup ruleGroup) {
        try {
            // Create a temporary AudienceSegment to hold the rule logic for evaluation
            AudienceSegment tempSegment = new AudienceSegment();
            tempSegment.setRuleLogicJson(objectMapper.writeValueAsString(ruleGroup)); // Serialize RuleGroup to JSON string

            int audienceSize = audienceSegmentService.previewAudienceSize(tempSegment);
            return new ResponseEntity<>(audienceSize, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            logger.error("Error processing rule group JSON for preview: {}", e.getMessage(), e);
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST); // Or HttpStatus.UNPROCESSABLE_ENTITY
        } catch (Exception e) {
            logger.error("Error previewing audience size: {}", e.getMessage(), e);
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Optional: DELETE and PUT/PATCH for segments

    /**
     * DELETE /api/audience-segments/{id}
     * Deletes an audience segment by its ID.
     * @param id The ID of the audience segment to delete.
     * @return 204 No Content if successful, 404 Not Found if segment doesn't exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAudienceSegment(@PathVariable Long id) {
        // You might add checks here if the segment is actively used by a campaign
        Optional<AudienceSegment> segment = audienceSegmentService.getAudienceSegmentById(id);
        if (segment.isPresent()) {
            audienceSegmentService.deleteAudienceSegment(id); // You'll need to add this method to your service
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // You'll need to add a deleteById method to AudienceSegmentRepository and AudienceSegmentService
    // In AudienceSegmentRepository: void deleteById(Long id);
    // In AudienceSegmentService:
    // public void deleteAudienceSegment(Long id) {
    //     audienceSegmentRepository.deleteById(id);
    // }
}