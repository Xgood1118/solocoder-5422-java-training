package com.company.training.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnlineCourse extends Course {
    private String platform;
    private String courseLink;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean apiSyncEnabled;
    private String platformAccount;
}
