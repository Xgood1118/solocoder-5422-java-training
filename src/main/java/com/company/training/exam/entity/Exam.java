package com.company.training.exam.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    private String id;
    private String courseId;
    private String title;
    private String description;
    private Integer totalScore;
    private Integer passScore;
    private Integer durationMinutes;
    private List<ExamQuestion> questions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean published;
    private LocalDateTime createdAt;
}
