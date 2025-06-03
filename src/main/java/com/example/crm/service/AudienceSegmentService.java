package com.example.crm.service;

import com.example.crm.entity.AudienceSegment;
import com.example.crm.entity.Customer;
import com.example.crm.entity.segmentation.Condition;
import com.example.crm.entity.segmentation.LogicalOperator;
import com.example.crm.entity.segmentation.Operator;
import com.example.crm.entity.segmentation.RuleGroup;
import com.example.crm.repository.AudienceSegmentRepository;
import com.example.crm.repository.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AudienceSegmentService {

    private static final Logger logger = LoggerFactory.getLogger(AudienceSegmentService.class);

    private final AudienceSegmentRepository audienceSegmentRepository;
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AudienceSegmentService(
            AudienceSegmentRepository audienceSegmentRepository,
            CustomerRepository customerRepository,
            ObjectMapper objectMapper) {
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.customerRepository = customerRepository;
        this.objectMapper = objectMapper;
    }

    public AudienceSegment saveAudienceSegment(AudienceSegment audienceSegment) {
        return audienceSegmentRepository.save(audienceSegment);
    }

    public List<AudienceSegment> getAllAudienceSegments() {
        return audienceSegmentRepository.findAll();
    }

    public Optional<AudienceSegment> getAudienceSegmentById(Long id) {
        return audienceSegmentRepository.findById(id);
    }

    public int previewAudienceSize(AudienceSegment audienceSegment) throws JsonProcessingException {
        if (audienceSegment.getRuleLogicJson() == null || audienceSegment.getRuleLogicJson().isEmpty()) {
            return 0;
        }

        RuleGroup ruleGroup = objectMapper.readValue(audienceSegment.getRuleLogicJson(), RuleGroup.class);
        List<Customer> allCustomers = customerRepository.findAll();

        int audienceSize = 0;
        for (Customer customer : allCustomers) {
            if (evaluateRuleGroup(ruleGroup, customer)) {
                audienceSize++;
            }
        }
        return audienceSize;
    }

    private boolean evaluateRuleGroup(RuleGroup ruleGroup, Customer customer) {
        boolean groupResult = ruleGroup.getLogicalOperator() == LogicalOperator.AND;

        if (ruleGroup.getConditions() != null) {
            for (Condition condition : ruleGroup.getConditions()) {
                boolean conditionEval = evaluateCondition(condition, customer);
                if (ruleGroup.getLogicalOperator() == LogicalOperator.AND) {
                    groupResult = groupResult && conditionEval;
                } else {
                    groupResult = groupResult || conditionEval;
                }
            }
        }

        if (ruleGroup.getNestedRuleGroups() != null) {
            for (RuleGroup nestedGroup : ruleGroup.getNestedRuleGroups()) {
                boolean nestedGroupEval = evaluateRuleGroup(nestedGroup, customer);
                if (ruleGroup.getLogicalOperator() == LogicalOperator.AND) {
                    groupResult = groupResult && nestedGroupEval;
                } else {
                    groupResult = groupResult || nestedGroupEval;
                }
            }
        }

        if ((ruleGroup.getConditions() == null || ruleGroup.getConditions().isEmpty()) &&
                (ruleGroup.getNestedRuleGroups() == null || ruleGroup.getNestedRuleGroups().isEmpty())) {
            return ruleGroup.getLogicalOperator() == LogicalOperator.AND; // Empty AND is true, empty OR is false
        }

        return groupResult;
    }

    private boolean evaluateCondition(Condition condition, Customer customer) {
        String field = condition.getField();
        Operator operator = condition.getOperator();
        String value = condition.getValue();

        try {
            switch (field) {
                case "name":
                    return applyStringOperator(customer.getName(), operator, value);
                case "email":
                    return applyStringOperator(customer.getEmail(), operator, value);
                case "phone":
                    return applyStringOperator(customer.getPhone(), operator, value);
                case "totalSpend":
                    return applyNumberOperator(customer.getTotalSpend(), operator, Double.parseDouble(value));
                case "visitCount":
                    return applyNumberOperator(customer.getVisitCount(), operator, Integer.parseInt(value));
                case "lastVisitDate":
                    if (operator == Operator.INACTIVE_FOR_DAYS) {
                        if (customer.getLastVisitDate() == null) {
                            return true; // Consider inactive if no last visit recorded
                        }
                        long daysInactive = ChronoUnit.DAYS.between(customer.getLastVisitDate(), LocalDate.now());
                        return daysInactive >= Long.parseLong(value);
                    }
                    return false; // Unsupported operator for lastVisitDate other than INACTIVE_FOR_DAYS
                default:
                    logger.warn("Unknown or unsupported field for segmentation: {}", field);
                    return false;
            }
        } catch (NumberFormatException e) {
            logger.error("Error parsing number value for field {}: {}", field, value, e);
            return false;
        } catch (Exception e) {
            logger.error("Error evaluating condition for field {}: {}", field, e.getMessage(), e);
            return false;
        }
    }

    private boolean applyStringOperator(String customerValue, Operator operator, String ruleValue) {
        if (customerValue == null) {
            return operator == Operator.IS_NULL;
        }
        if (operator == Operator.IS_NOT_NULL) {
            return true;
        }

        switch (operator) {
            case EQUALS: return customerValue.equalsIgnoreCase(ruleValue);
            case NOT_EQUALS: return !customerValue.equalsIgnoreCase(ruleValue);
            case CONTAINS: return customerValue.toLowerCase().contains(ruleValue.toLowerCase());
            case STARTS_WITH: return customerValue.toLowerCase().startsWith(ruleValue.toLowerCase());
            case ENDS_WITH: return customerValue.toLowerCase().endsWith(ruleValue.toLowerCase());
            case IN:
                List<String> valuesInList = List.of(ruleValue.split(",")).stream().map(String::trim).collect(Collectors.toList());
                return valuesInList.stream().anyMatch(v -> customerValue.equalsIgnoreCase(v));
            case NOT_IN:
                List<String> valuesNotInList = List.of(ruleValue.split(",")).stream().map(String::trim).collect(Collectors.toList());
                return valuesNotInList.stream().noneMatch(v -> customerValue.equalsIgnoreCase(v));
            default:
                logger.warn("Unsupported operator {} for String field", operator);
                return false;
        }
    }

    private <T extends Number> boolean applyNumberOperator(T customerValue, Operator operator, T ruleValue) {
        if (customerValue == null) {
            return operator == Operator.IS_NULL;
        }
        if (operator == Operator.IS_NOT_NULL) {
            return true;
        }

        double custVal = customerValue.doubleValue();
        double ruleVal = ruleValue.doubleValue();

        switch (operator) {
            case EQUALS: return custVal == ruleVal;
            case NOT_EQUALS: return custVal != ruleVal;
            case GREATER_THAN: return custVal > ruleVal;
            case LESS_THAN: return custVal < ruleVal;
            default:
                logger.warn("Unsupported operator {} for Number field", operator);
                return false;
        }
    }

    // ... (inside AudienceSegmentService class)
    public void deleteAudienceSegment(Long id) {
        audienceSegmentRepository.deleteById(id);
    }
}