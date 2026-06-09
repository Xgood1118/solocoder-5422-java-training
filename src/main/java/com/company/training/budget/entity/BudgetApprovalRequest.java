package com.company.training.budget.entity;

import com.company.training.common.enums.BudgetApprovalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetApprovalRequest {
    private String id;
    private String courseId;
    private String departmentId;
    private BigDecimal requestedAmount;
    private BigDecimal overBudgetAmount;
    private BudgetApprovalStatus status;
    private String requesterId;
    private String approverId;
    private String reason;
    private String approvalComment;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
}
