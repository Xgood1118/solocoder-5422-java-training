package com.company.training.department.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private String id;
    private String name;
    private String managerId;
    private Integer headCount;
    private BigDecimal annualBudget;
    private Year budgetYear;
}
