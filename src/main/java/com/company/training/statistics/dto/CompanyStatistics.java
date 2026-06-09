package com.company.training.statistics.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatistics {
    private int year;
    private int quarter;
    private int totalDepartments;
    private int totalEmployees;
    private int totalCourses;
    private int completedCourses;
    private int totalEnrollments;
    private int totalParticipants;
    private double overallParticipationRate;
    private double overallAttendanceRate;
    private double overallPassRate;
    private BigDecimal totalBudget;
    private BigDecimal totalUsedBudget;
    private BigDecimal totalOverspent;
    private double totalCreditHours;
    private int totalCertificates;
    private Map<String, Integer> coursesByType;
    private List<DepartmentStatistics> departmentBreakdown;
}
