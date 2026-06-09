package com.company.training.cert.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    private String id;
    private String certificateNo;
    private String employeeId;
    private String employeeName;
    private String courseId;
    private String courseName;
    private Double creditHours;
    private Integer examScore;
    private LocalDateTime issuedAt;
    private LocalDateTime validUntil;
    private String issuer;
    private String certificateUrl;
    private String trainingRecordId;
}
