package com.example.crm.entity; // <--- CHANGED PACKAGE

import com.example.crm.entity.segmentation.RuleGroup; // <--- UPDATED IMPORT for RuleGroup
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audience_segments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudienceSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Lob
    @Column(name = "rule_logic_json", nullable = false)
    private String ruleLogicJson;

    private Integer previewAudienceSize;
    private LocalDateTime createdAt;
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}