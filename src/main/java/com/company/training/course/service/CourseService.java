package com.company.training.course.service;

import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.CourseType;
import com.company.training.course.entity.*;
import com.company.training.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        if (course.getId() == null || course.getId().isEmpty()) {
            course.setId(UUID.randomUUID().toString());
        }
        if (course.getStatus() == null) {
            course.setStatus(CourseStatus.DRAFT);
        }
        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        if (course.getWaitlistCapacity() == null) {
            course.setWaitlistCapacity(course.getCapacity());
        }
        if (course.getRequiresExam() && course.getCreditHours() == null) {
            course.setCreditHours(0.0);
        }
        return courseRepository.save(course);
    }

    public InternalTechCourse createInternalTechCourse(InternalTechCourse course) {
        course.setCourseType(CourseType.INTERNAL_TECH);
        return (InternalTechCourse) createCourse(course);
    }

    public InternalSoftSkillCourse createInternalSoftSkillCourse(InternalSoftSkillCourse course) {
        course.setCourseType(CourseType.INTERNAL_SOFT_SKILL);
        return (InternalSoftSkillCourse) createCourse(course);
    }

    public ExternalCourse createExternalCourse(ExternalCourse course) {
        course.setCourseType(CourseType.EXTERNAL);
        return (ExternalCourse) createCourse(course);
    }

    public OnlineCourse createOnlineCourse(OnlineCourse course) {
        course.setCourseType(CourseType.ONLINE);
        return (OnlineCourse) createCourse(course);
    }

    public BookClubCourse createBookClubCourse(BookClubCourse course) {
        course.setCourseType(CourseType.BOOK_CLUB);
        return (BookClubCourse) createCourse(course);
    }

    public Course getCourse(String id) {
        return courseRepository.findById(id);
    }

    public Collection<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByType(CourseType courseType) {
        return courseRepository.findByCourseType(courseType);
    }

    public List<Course> getCoursesByDepartment(String departmentId) {
        return courseRepository.findByDepartmentId(departmentId);
    }

    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    public Course publishCourse(String courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new IllegalStateException("只有草稿状态的课程可以发布");
        }
        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Course startCourse(String courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("只有已发布的课程可以开始");
        }
        course.setStatus(CourseStatus.ONGOING);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Course completeCourse(String courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getStatus() != CourseStatus.ONGOING) {
            throw new IllegalStateException("只有进行中的课程可以结束");
        }
        course.setStatus(CourseStatus.COMPLETED);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Course cancelCourse(String courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        course.setStatus(CourseStatus.CANCELLED);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, Course course) {
        Course existing = courseRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("课程不存在: " + id);
        }
        course.setId(id);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    public boolean isCourseFull(String courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        return false;
    }
}
