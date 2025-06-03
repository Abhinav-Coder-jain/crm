package com.example.crm.util;

import com.example.crm.entity.Customer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RuleEngine {

    // Simple regex to parse rules like "propertyName operator value"
    // Supports: totalSpend, visitCount, lastVisitDate
    // Operators: >, <, >=, <=, ==
    // Example: "totalSpend > 10000 AND lastVisitDate < '2024-01-01'"
    private static final Pattern RULE_PATTERN = Pattern.compile(
            "\\b(totalSpend|visitCount|lastVisitDate)\\s*(>|<|>=|<=|==)\\s*('?\"?([^'\"\\s]+)'?\"?|\\d+\\.?\\d*)\\b"
    );

    public static Set<Customer> evaluateRules(String segmentRules, List<Customer> allCustomers) {
        if (segmentRules == null || segmentRules.trim().isEmpty()) {
            return new HashSet<>(allCustomers); // If no rules, all customers are the audience
        }

        // Split by major logical operators (AND/OR). For simplicity, we'll only handle AND for now.
        // A more robust solution would build an Abstract Syntax Tree (AST).
        String[] individualRules = segmentRules.split("\\s+AND\\s+");

        // Start with all customers and filter them down
        Set<Customer> filteredCustomers = new HashSet<>(allCustomers);

        for (String rule : individualRules) {
            Matcher matcher = RULE_PATTERN.matcher(rule.trim());
            if (matcher.find()) {
                String property = matcher.group(1);
                String operator = matcher.group(2);
                String valueStr = matcher.group(4); // The value part of the rule

                Predicate<Customer> predicate = createPredicate(property, operator, valueStr);

                // Apply the current rule's predicate to the already filtered set
                filteredCustomers = filteredCustomers.stream()
                        .filter(predicate)
                        .collect(Collectors.toCollection(HashSet::new));
            } else {
                // If a rule cannot be parsed, log a warning or throw an exception.
                // For this assignment, we'll just ignore it or treat it as not matching.
                System.err.println("Warning: Could not parse rule: " + rule);
            }
        }

        return filteredCustomers;
    }

    private static Predicate<Customer> createPredicate(String property, String operator, String valueStr) {
        switch (property) {
            case "totalSpend":
                Double spendValue = Double.parseDouble(valueStr);
                return customer -> {
                    if (customer.getTotalSpend() == null) return false;
                    return switch (operator) {
                        case ">" -> customer.getTotalSpend() > spendValue;
                        case "<" -> customer.getTotalSpend() < spendValue;
                        case ">=" -> customer.getTotalSpend() >= spendValue;
                        case "<=" -> customer.getTotalSpend() <= spendValue;
                        case "==" -> customer.getTotalSpend().equals(spendValue);
                        default -> false;
                    };
                };
            case "visitCount":
                Integer visitValue = Integer.parseInt(valueStr);
                return customer -> {
                    if (customer.getVisitCount() == null) return false;
                    return switch (operator) {
                        case ">" -> customer.getVisitCount() > visitValue;
                        case "<" -> customer.getVisitCount() < visitValue;
                        case ">=" -> customer.getVisitCount() >= visitValue;
                        case "<=" -> customer.getVisitCount() <= visitValue;
                        case "==" -> customer.getVisitCount().equals(visitValue);
                        default -> false;
                    };
                };
            case "lastVisitDate":
                LocalDate dateValue = LocalDate.parse(valueStr); // Expecting YYYY-MM-DD
                return customer -> {
                    if (customer.getLastVisitDate() == null) return false;
                    return switch (operator) {
                        case ">" -> customer.getLastVisitDate().isAfter(dateValue);
                        case "<" -> customer.getLastVisitDate().isBefore(dateValue);
                        case ">=" -> customer.getLastVisitDate().isAfter(dateValue) || customer.getLastVisitDate().isEqual(dateValue);
                        case "<=" -> customer.getLastVisitDate().isBefore(dateValue) || customer.getLastVisitDate().isEqual(dateValue);
                        case "==" -> customer.getLastVisitDate().isEqual(dateValue);
                        default -> false;
                    };
                };
            default:
                return customer -> false; // No match for property, so filter out
        }
    }
}