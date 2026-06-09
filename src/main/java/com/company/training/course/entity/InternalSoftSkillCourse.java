package com.company.training.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class InternalSoftSkillCourse extends Course {
    private String lecturer;
    private boolean isExternalLecturer;
    private String location;
    private String coursewareUrl;
    private String skillCategory;
}
