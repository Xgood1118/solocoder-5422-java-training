package com.company.training.budget.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetUsageRecord {
    private String id;
    private String departmentId;
    private String courseId;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private BigDecimal settledCost;
    private int enrolledCount;
    private int attendedCount;
    private int passedCount;
    private int absentCount;
    private LocalDateTime createdAt;
    private LocalDateTime settledAt;
    private boolean settled;
}
