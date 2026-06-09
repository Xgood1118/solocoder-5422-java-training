package com.company.training.budget.entity;

import com.company.training.common.enums.BudgetApprovalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentBudget {
    private String id;
    private String departmentId;
    private Year fiscalYear;
    private BigDecimal totalBudget;
    private BigDecimal usedBudget;
    private BigDecimal reservedBudget;
    private BigDecimal overspentBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
