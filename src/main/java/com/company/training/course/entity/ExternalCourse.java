package com.company.training.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCourse extends Course {
    private String trainingInstitution;
    private String institutionContact;
    private String reimbursementRule;
    private boolean requiresEmployeeAdvance;
    private String externalLocation;
}
