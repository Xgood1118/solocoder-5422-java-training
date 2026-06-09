package com.company.training.enrollment.controller;

import com.company.training.common.enums.EnrollmentStatus;
import com.company.training.common.response.ApiResponse;
import com.company.training.enrollment.entity.Enrollment;
import com.company.training.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ApiResponse<Enrollment> enroll(@RequestBody Map<String, String> request) {
        try {
            String courseId = request.get("courseId");
            String employeeId = request.get("employeeId");
            return ApiResponse.success(enrollmentService.enroll(courseId, employeeId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Enrollment> cancel(@PathVariable String id, @RequestBody(required = false) Map<String, String> request) {
        try {
            String cancelledBy = request != null ? request.get("cancelledBy") : "system";
            return ApiResponse.success(enrollmentService.cancelEnrollment(id, cancelledBy));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Enrollment> getById(@PathVariable String id) {
        Enrollment enrollment = enrollmentService.getEnrollment(id);
        if (enrollment == null) {
            return ApiResponse.error(404, "报名记录不存在");
        }
        return ApiResponse.success(enrollment);
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Enrollment>> getByCourse(@PathVariable String courseId) {
        return ApiResponse.success(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/status/{status}")
    public ApiResponse<List<Enrollment>> getByCourseAndStatus(
            @PathVariable String courseId,
            @PathVariable EnrollmentStatus status) {
        return ApiResponse.success(enrollmentService.getEnrollmentsByCourseAndStatus(courseId, status));
    }

    @GetMapping("/course/{courseId}/waitlist")
    public ApiResponse<List<Enrollment>> getWaitlist(@PathVariable String courseId) {
        return ApiResponse.success(enrollmentService.getWaitlist(courseId));
    }

    @GetMapping("/course/{courseId}/stats")
    public ApiResponse<Map<String, Long>> getCourseStats(@PathVariable String courseId) {
        long enrolled = enrollmentService.getEnrolledCount(courseId);
        long waitlisted = enrollmentService.getWaitlistCount(courseId);
        return ApiResponse.success(Map.of(
                "enrolled", enrolled,
                "waitlisted", waitlisted
        ));
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<Enrollment>> getByEmployee(@PathVariable String employeeId) {
        return ApiResponse.success(enrollmentService.getEnrollmentsByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public ApiResponse<List<Enrollment>> getByEmployeeAndStatus(
            @PathVariable String employeeId,
            @PathVariable EnrollmentStatus status) {
        return ApiResponse.success(enrollmentService.getEnrollmentsByEmployeeAndStatus(employeeId, status));
    }

    @GetMapping("/by-course-and-employee")
    public ApiResponse<Enrollment> getByCourseAndEmployee(
            @RequestParam String courseId,
            @RequestParam String employeeId) {
        Enrollment enrollment = enrollmentService.getEnrollmentByCourseAndEmployee(courseId, employeeId);
        if (enrollment == null) {
            return ApiResponse.error(404, "未找到报名记录");
        }
        return ApiResponse.success(enrollment);
    }
}
