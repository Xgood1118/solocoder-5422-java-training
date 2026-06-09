package com.company.training.exam.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestion {
    private String id;
    private String questionText;
    private String type;
    private List<String> options;
    private String correctAnswer;
    private Integer score;
}
