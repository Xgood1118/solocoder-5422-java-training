package com.company.training.statistics.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentStatistics {
    private String departmentId;
    private String departmentName;
    private int year;
    private int quarter;
    private int totalCourses;
    private int completedCourses;
    private int ongoingCourses;
    private int totalEnrollments;
    private int uniqueParticipants;
    private int totalEmployees;
    private double participationRate;
    private double averageAttendanceRate;
    private double averageExamPassRate;
    private BigDecimal totalBudget;
    private BigDecimal usedBudget;
    private BigDecimal overspentBudget;
    private double budgetUsageRate;
    private double averageEvaluationScore;
    private double totalCreditHours;
    private Map<String, Integer> coursesByType;
    private int newCertificates;
}
