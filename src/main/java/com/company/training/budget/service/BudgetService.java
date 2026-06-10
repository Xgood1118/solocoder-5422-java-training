package com.company.training.budget.service;

import com.company.training.budget.entity.BudgetApprovalRequest;
import com.company.training.budget.entity.BudgetUsageRecord;
import com.company.training.budget.entity.DepartmentBudget;
import com.company.training.budget.repository.BudgetApprovalRequestRepository;
import com.company.training.budget.repository.BudgetUsageRecordRepository;
import com.company.training.budget.repository.DepartmentBudgetRepository;
import com.company.training.common.enums.*;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.department.entity.Department;
import com.company.training.department.service.DepartmentService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.enrollment.service.EnrollmentService;
import com.company.training.record.entity.TrainingRecord;
import com.company.training.record.service.TrainingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final DepartmentBudgetRepository budgetRepository;
    private final BudgetApprovalRequestRepository approvalRepository;
    private final BudgetUsageRecordRepository usageRepository;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final EmployeeService employeeService;
    private final EnrollmentService enrollmentService;
    private final TrainingRecordService recordService;

    public DepartmentBudget allocateBudget(String departmentId, Year fiscalYear, BigDecimal amount) {
        Department dept = departmentService.getDepartment(departmentId);
        if (dept == null) {
            throw new IllegalArgumentException("部门不存在: " + departmentId);
        }

        DepartmentBudget existing = budgetRepository.findByDepartmentIdAndYear(departmentId, fiscalYear);
        if (existing != null) {
            existing.setTotalBudget(amount);
            existing.setUpdatedAt(LocalDateTime.now());
            return budgetRepository.save(existing);
        }

        DepartmentBudget budget = new DepartmentBudget();
        budget.setId(UUID.randomUUID().toString());
        budget.setDepartmentId(departmentId);
        budget.setFiscalYear(fiscalYear);
        budget.setTotalBudget(amount);
        budget.setUsedBudget(BigDecimal.ZERO);
        budget.setReservedBudget(BigDecimal.ZERO);
        budget.setOverspentBudget(BigDecimal.ZERO);
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        return budgetRepository.save(budget);
    }

    public DepartmentBudget getDepartmentBudget(String departmentId, Year fiscalYear) {
        return budgetRepository.findByDepartmentIdAndYear(departmentId, fiscalYear);
    }

    public List<DepartmentBudget> getBudgetsByYear(Year fiscalYear) {
        return budgetRepository.findByYear(fiscalYear);
    }

    public boolean checkBudget(String courseId) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getCostPerPerson() == null || course.getCostPerPerson().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        DepartmentBudget budget = budgetRepository.findByDepartmentIdAndYear(
                course.getDepartmentId(),
                Year.from(course.getStartTime() != null ? course.getStartTime() : LocalDateTime.now())
        );
        if (budget == null) {
            return false;
        }

        long enrolledCount = enrollmentService.getEnrolledCount(courseId);
        BigDecimal estimatedCost = course.getCostPerPerson().multiply(BigDecimal.valueOf(enrolledCount));
        BigDecimal available = budget.getTotalBudget()
                .subtract(budget.getUsedBudget())
                .subtract(budget.getReservedBudget());

        return available.compareTo(estimatedCost) >= 0;
    }

    public BudgetApprovalRequest requestSpecialApproval(String courseId, String requesterId, String reason) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }

        Employee requester = employeeService.getEmployee(requesterId);
        if (requester == null) {
            throw new IllegalArgumentException("申请人不存在: " + requesterId);
        }

        BudgetApprovalRequest existing = approvalRepository.findByCourseId(courseId);
        if (existing != null && existing.getStatus() == BudgetApprovalStatus.PENDING_SPECIAL_APPROVAL) {
            throw new IllegalStateException("该课程已有待审批的预算申请");
        }

        long enrolledCount = enrollmentService.getEnrolledCount(courseId);
        BigDecimal estimatedCost = course.getCostPerPerson() != null
                ? course.getCostPerPerson().multiply(BigDecimal.valueOf(enrolledCount))
                : BigDecimal.ZERO;

        DepartmentBudget budget = budgetRepository.findByDepartmentIdAndYear(
                course.getDepartmentId(),
                Year.from(course.getStartTime() != null ? course.getStartTime() : LocalDateTime.now())
        );
        BigDecimal overBudgetAmount = BigDecimal.ZERO;
        if (budget != null) {
            BigDecimal available = budget.getTotalBudget()
                    .subtract(budget.getUsedBudget())
                    .subtract(budget.getReservedBudget());
            if (estimatedCost.compareTo(available) > 0) {
                overBudgetAmount = estimatedCost.subtract(available);
            }
        }

        BudgetApprovalRequest request = new BudgetApprovalRequest();
        request.setId(UUID.randomUUID().toString());
        request.setCourseId(courseId);
        request.setDepartmentId(course.getDepartmentId());
        request.setRequestedAmount(estimatedCost);
        request.setOverBudgetAmount(overBudgetAmount);
        request.setStatus(BudgetApprovalStatus.PENDING_SPECIAL_APPROVAL);
        request.setRequesterId(requesterId);
        request.setReason(reason);
        request.setRequestedAt(LocalDateTime.now());

        return approvalRepository.save(request);
    }

    public BudgetApprovalRequest approveBudgetRequest(String requestId, String approverId, String comment) {
        BudgetApprovalRequest request = approvalRepository.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("审批申请不存在: " + requestId);
        }
        if (request.getStatus() != BudgetApprovalStatus.PENDING_SPECIAL_APPROVAL) {
            throw new IllegalStateException("该申请状态不允许审批");
        }

        request.setStatus(BudgetApprovalStatus.APPROVED);
        request.setApproverId(approverId);
        request.setApprovalComment(comment);
        request.setApprovedAt(LocalDateTime.now());

        return approvalRepository.save(request);
    }

    public BudgetApprovalRequest rejectBudgetRequest(String requestId, String approverId, String comment) {
        BudgetApprovalRequest request = approvalRepository.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("审批申请不存在: " + requestId);
        }
        if (request.getStatus() != BudgetApprovalStatus.PENDING_SPECIAL_APPROVAL) {
            throw new IllegalStateException("该申请状态不允许审批");
        }

        request.setStatus(BudgetApprovalStatus.REJECTED);
        request.setApproverId(approverId);
        request.setApprovalComment(comment);
        request.setApprovedAt(LocalDateTime.now());

        return approvalRepository.save(request);
    }

    public BudgetUsageRecord settleCourseBudget(String courseId) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getStatus() != CourseStatus.COMPLETED) {
            throw new IllegalStateException("只有已结束的课程才能进行费用结算");
        }

        BudgetUsageRecord existing = usageRepository.findByCourseId(courseId);
        if (existing != null && existing.isSettled()) {
            return existing;
        }

        List<TrainingRecord> records = recordService.getRecordsByCourse(courseId);
        int enrolledCount = (int) enrollmentService.getEnrolledCount(courseId);
        int attendedCount = 0;
        int passedCount = 0;
        int absentCount = 0;

        for (TrainingRecord record : records) {
            if (record.getCreditHours() != null && record.getCreditHours() > 0) {
                attendedCount++;
                if (record.isPassed()) {
                    passedCount++;
                }
            } else {
                absentCount++;
            }
        }

        BigDecimal costPerPerson = course.getCostPerPerson() != null ? course.getCostPerPerson() : BigDecimal.ZERO;
        BigDecimal actualCost = costPerPerson.multiply(BigDecimal.valueOf(attendedCount));
        BigDecimal settledCost = costPerPerson.multiply(BigDecimal.valueOf(passedCount));

        DepartmentBudget budget = budgetRepository.findByDepartmentIdAndYear(
                course.getDepartmentId(),
                Year.from(course.getEndTime() != null ? course.getEndTime() : LocalDateTime.now())
        );
        if (budget != null) {
            budget.setUsedBudget(budget.getUsedBudget().add(settledCost));
            BigDecimal overspent = actualCost.subtract(settledCost);
            if (overspent.compareTo(BigDecimal.ZERO) > 0) {
                budget.setOverspentBudget(budget.getOverspentBudget().add(overspent));
            }
            budget.setUpdatedAt(LocalDateTime.now());
            budgetRepository.save(budget);
        }

        BudgetUsageRecord usageRecord;
        if (existing != null) {
            usageRecord = existing;
        } else {
            usageRecord = new BudgetUsageRecord();
            usageRecord.setId(UUID.randomUUID().toString());
            usageRecord.setCreatedAt(LocalDateTime.now());
        }
        usageRecord.setDepartmentId(course.getDepartmentId());
        usageRecord.setCourseId(courseId);
        usageRecord.setEstimatedCost(costPerPerson.multiply(BigDecimal.valueOf(enrolledCount)));
        usageRecord.setActualCost(actualCost);
        usageRecord.setSettledCost(settledCost);
        usageRecord.setEnrolledCount(enrolledCount);
        usageRecord.setAttendedCount(attendedCount);
        usageRecord.setPassedCount(passedCount);
        usageRecord.setAbsentCount(absentCount);
        usageRecord.setSettled(true);
        usageRecord.setSettledAt(LocalDateTime.now());

        return usageRepository.save(usageRecord);
    }

    public BudgetUsageRecord getUsageRecord(String id) {
        return usageRepository.findById(id);
    }

    public BudgetUsageRecord getUsageByCourse(String courseId) {
        return usageRepository.findByCourseId(courseId);
    }

    public List<BudgetUsageRecord> getUsageByDepartment(String departmentId) {
        return usageRepository.findByDepartmentId(departmentId);
    }

    public BudgetApprovalRequest getApprovalRequest(String id) {
        return approvalRepository.findById(id);
    }

    public BudgetApprovalRequest getApprovalByCourse(String courseId) {
        return approvalRepository.findByCourseId(courseId);
    }

    public List<BudgetApprovalRequest> getApprovalsByDepartment(String departmentId) {
        return approvalRepository.findByDepartmentId(departmentId);
    }

    public List<BudgetApprovalRequest> getApprovalsByStatus(BudgetApprovalStatus status) {
        return approvalRepository.findByStatus(status);
    }
}
