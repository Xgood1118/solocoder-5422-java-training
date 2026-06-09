package com.company.training.exam.entity;

import com.company.training.common.enums.ExamResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempt {
    private String id;
    private String examId;
    private String courseId;
    private String employeeId;
    private Integer attemptNumber;
    private Integer score;
    private ExamResult result;
    private Map<String, String> answers;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private boolean isRetake;
}
