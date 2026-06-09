package com.company.training.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BookClubCourse extends Course {
    private String bookName;
    private String bookAuthor;
    private String discussionTopic;
    private String hostEmployeeId;
    private String location;
}
