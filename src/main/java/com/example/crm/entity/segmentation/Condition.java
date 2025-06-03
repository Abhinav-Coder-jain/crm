package com.example.crm.entity.segmentation; // <--- CHANGED PACKAGE

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    private String field;
    private Operator operator;
    private String value;
}