package com.company.training.employee.entity;

import com.company.training.common.enums.PositionType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private String id;
    private String name;
    private String employeeNo;
    private String departmentId;
    private PositionType position;
    private String email;
    private String phone;
    private String facePhotoUrl;
    private boolean isDepartmentManager;
    private boolean isHrManager;
}
