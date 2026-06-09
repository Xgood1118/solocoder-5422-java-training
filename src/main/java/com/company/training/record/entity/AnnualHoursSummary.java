package com.company.training.record.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualHoursSummary {
    private String employeeId;
    private String employeeName;
    private String departmentId;
    private String departmentName;
    private String positionType;
    private int year;
    private double totalHours;
    private double internalHours;
    private double externalHours;
    private double onlineHours;
    private double bookClubHours;
    private int requiredHours;
    private int requiredExternalHours;
    private double completionRate;
    private boolean meetsRequirement;
    private boolean meetsExternalRequirement;
}
