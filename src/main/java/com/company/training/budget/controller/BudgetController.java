package com.company.training.budget.controller;

import com.company.training.budget.entity.BudgetApprovalRequest;
import com.company.training.budget.entity.BudgetUsageRecord;
import com.company.training.budget.entity.DepartmentBudget;
import com.company.training.budget.service.BudgetService;
import com.company.training.common.enums.BudgetApprovalStatus;
import com.company.training.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping("/allocate")
    public ApiResponse<DepartmentBudget> allocateBudget(@RequestBody Map<String, Object> request) {
        try {
            String departmentId = (String) request.get("departmentId");
            Year fiscalYear = Year.parse((String) request.get("fiscalYear"));
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            return ApiResponse.success(budgetService.allocateBudget(departmentId, fiscalYear, amount));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/department/{departmentId}/year/{year}")
    public ApiResponse<DepartmentBudget> getDepartmentBudget(
            @PathVariable String departmentId,
            @PathVariable int year) {
        DepartmentBudget budget = budgetService.getDepartmentBudget(departmentId, Year.of(year));
        if (budget == null) {
            return ApiResponse.error(404, "未找到部门预算");
        }
        return ApiResponse.success(budget);
    }

    @GetMapping("/year/{year}")
    public ApiResponse<List<DepartmentBudget>> getBudgetsByYear(@PathVariable int year) {
        return ApiResponse.success(budgetService.getBudgetsByYear(Year.of(year)));
    }

    @GetMapping("/check/{courseId}")
    public ApiResponse<Boolean> checkBudget(@PathVariable String courseId) {
        try {
            return ApiResponse.success(budgetService.checkBudget(courseId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/approval/request")
    public ApiResponse<BudgetApprovalRequest> requestApproval(@RequestBody Map<String, String> request) {
        try {
            String courseId = request.get("courseId");
            String requesterId = request.get("requesterId");
            String reason = request.get("reason");
            return ApiResponse.success(
                    budgetService.requestSpecialApproval(courseId, requesterId, reason)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/approval/{id}/approve")
    public ApiResponse<BudgetApprovalRequest> approve(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String approverId = request.get("approverId");
            String comment = request.get("comment");
            return ApiResponse.success(budgetService.approveBudgetRequest(id, approverId, comment));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/approval/{id}/reject")
    public ApiResponse<BudgetApprovalRequest> reject(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String approverId = request.get("approverId");
            String comment = request.get("comment");
            return ApiResponse.success(budgetService.rejectBudgetRequest(id, approverId, comment));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/approval/{id}")
    public ApiResponse<BudgetApprovalRequest> getApproval(@PathVariable String id) {
        BudgetApprovalRequest approval = budgetService.getApprovalRequest(id);
        if (approval == null) {
            return ApiResponse.error(404, "审批申请不存在");
        }
        return ApiResponse.success(approval);
    }

    @GetMapping("/approval/by-course/{courseId}")
    public ApiResponse<BudgetApprovalRequest> getApprovalByCourse(@PathVariable String courseId) {
        BudgetApprovalRequest approval = budgetService.getApprovalByCourse(courseId);
        if (approval == null) {
            return ApiResponse.error(404, "未找到相关审批");
        }
        return ApiResponse.success(approval);
    }

    @GetMapping("/approval/by-department/{departmentId}")
    public ApiResponse<List<BudgetApprovalRequest>> getApprovalsByDepartment(@PathVariable String departmentId) {
        return ApiResponse.success(budgetService.getApprovalsByDepartment(departmentId));
    }

    @GetMapping("/approval/status/{status}")
    public ApiResponse<List<BudgetApprovalRequest>> getApprovalsByStatus(@PathVariable BudgetApprovalStatus status) {
        return ApiResponse.success(budgetService.getApprovalsByStatus(status));
    }

    @PostMapping("/settle/{courseId}")
    public ApiResponse<BudgetUsageRecord> settle(@PathVariable String courseId) {
        try {
            return ApiResponse.success(budgetService.settleCourseBudget(courseId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/usage/{id}")
    public ApiResponse<BudgetUsageRecord> getUsage(@PathVariable String id) {
        BudgetUsageRecord record = budgetService.getUsageRecord(id);
        if (record == null) {
            return ApiResponse.error(404, "费用记录不存在");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/usage/by-course/{courseId}")
    public ApiResponse<BudgetUsageRecord> getUsageByCourse(@PathVariable String courseId) {
        BudgetUsageRecord record = budgetService.getUsageByCourse(courseId);
        if (record == null) {
            return ApiResponse.error(404, "未找到费用记录");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/usage/by-department/{departmentId}")
    public ApiResponse<List<BudgetUsageRecord>> getUsageByDepartment(@PathVariable String departmentId) {
        return ApiResponse.success(budgetService.getUsageByDepartment(departmentId));
    }
}
