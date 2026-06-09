package com.company.training.attendance.entity;

import com.company.training.common.enums.AttendanceStatus;
import com.company.training.common.enums.SignInMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
    private String id;
    private String courseId;
    private String employeeId;
    private String enrollmentId;
    private AttendanceStatus status;
    private SignInMethod signInMethod;
    private LocalDateTime signInTime;
    private Double signInLatitude;
    private Double signInLongitude;
    private String gpsValidationResult;
    private String faceRecognitionResult;
    private String qrTokenId;
    private String sessionId;
}
