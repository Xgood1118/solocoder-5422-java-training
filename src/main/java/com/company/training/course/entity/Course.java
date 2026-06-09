package com.company.training.course.entity;

import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.CourseType;
import com.company.training.common.enums.SignInMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String id;
    private String name;
    private CourseType courseType;
    private CourseStatus status;
    private String departmentId;
    private String description;
    private Integer capacity;
    private Integer waitlistCapacity;
    private Double creditHours;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SignInMethod signInMethod;
    private BigDecimal costPerPerson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean requiresExam;
    private String organizerId;
}
