package com.example.crm.entity.segmentation; // <--- CHANGED PACKAGE

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleGroup {
    private LogicalOperator logicalOperator;
    private List<Condition> conditions;
    private List<RuleGroup> nestedRuleGroups;
}