package com.company.training.enrollment.entity;

import com.company.training.common.enums.EnrollmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private String id;
    private String courseId;
    private String employeeId;
    private EnrollmentStatus status;
    private Integer waitlistPosition;
    private LocalDateTime enrolledAt;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
}
