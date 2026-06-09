package com.company.training.record.entity;

import com.company.training.common.enums.CourseType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRecord {
    private String id;
    private String employeeId;
    private String courseId;
    private String courseName;
    private CourseType courseType;
    private String enrollmentId;
    private String attendanceId;
    private String examAttemptId;
    private Double creditHours;
    private Integer examScore;
    private boolean passed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private String departmentId;
    private Double evaluationScore;
}
