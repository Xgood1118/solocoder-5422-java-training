package com.company.training.course.controller;

import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.CourseType;
import com.company.training.common.response.ApiResponse;
import com.company.training.course.entity.*;
import com.company.training.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/internal-tech")
    public ApiResponse<InternalTechCourse> createInternalTech(@RequestBody InternalTechCourse course) {
        return ApiResponse.success(courseService.createInternalTechCourse(course));
    }

    @PostMapping("/internal-soft-skill")
    public ApiResponse<InternalSoftSkillCourse> createInternalSoftSkill(@RequestBody InternalSoftSkillCourse course) {
        return ApiResponse.success(courseService.createInternalSoftSkillCourse(course));
    }

    @PostMapping("/external")
    public ApiResponse<ExternalCourse> createExternal(@RequestBody ExternalCourse course) {
        return ApiResponse.success(courseService.createExternalCourse(course));
    }

    @PostMapping("/online")
    public ApiResponse<OnlineCourse> createOnline(@RequestBody OnlineCourse course) {
        return ApiResponse.success(courseService.createOnlineCourse(course));
    }

    @PostMapping("/book-club")
    public ApiResponse<BookClubCourse> createBookClub(@RequestBody BookClubCourse course) {
        return ApiResponse.success(courseService.createBookClubCourse(course));
    }

    @GetMapping("/{id}")
    public ApiResponse<Course> getById(@PathVariable String id) {
        Course course = courseService.getCourse(id);
        if (course == null) {
            return ApiResponse.error(404, "课程不存在");
        }
        return ApiResponse.success(course);
    }

    @GetMapping
    public ApiResponse<Collection<Course>> getAll() {
        return ApiResponse.success(courseService.getAllCourses());
    }

    @GetMapping("/by-type/{courseType}")
    public ApiResponse<List<Course>> getByType(@PathVariable CourseType courseType) {
        return ApiResponse.success(courseService.getCoursesByType(courseType));
    }

    @GetMapping("/by-department/{departmentId}")
    public ApiResponse<List<Course>> getByDepartment(@PathVariable String departmentId) {
        return ApiResponse.success(courseService.getCoursesByDepartment(departmentId));
    }

    @GetMapping("/by-status/{status}")
    public ApiResponse<List<Course>> getByStatus(@PathVariable CourseStatus status) {
        return ApiResponse.success(courseService.getCoursesByStatus(status));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Course> publish(@PathVariable String id) {
        try {
            return ApiResponse.success(courseService.publishCourse(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public ApiResponse<Course> start(@PathVariable String id) {
        try {
            return ApiResponse.success(courseService.startCourse(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Course> complete(@PathVariable String id) {
        try {
            return ApiResponse.success(courseService.completeCourse(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Course> cancel(@PathVariable String id) {
        try {
            return ApiResponse.success(courseService.cancelCourse(id));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Course> update(@PathVariable String id, @RequestBody Course course) {
        try {
            return ApiResponse.success(courseService.updateCourse(id, course));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ApiResponse.success();
    }
}
