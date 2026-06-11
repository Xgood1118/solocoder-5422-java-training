package com.company.training.record.controller;

import com.company.training.common.response.ApiResponse;
import com.company.training.record.entity.AnnualHoursSummary;
import com.company.training.record.entity.TrainingRecord;
import com.company.training.record.service.TrainingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training-records")
@RequiredArgsConstructor
public class TrainingRecordController {

    private final TrainingRecordService recordService;

    @PostMapping("/generate")
    public ApiResponse<TrainingRecord> generateRecord(@RequestBody Map<String, String> request) {
        try {
            String courseId = request.get("courseId");
            String employeeId = request.get("employeeId");
            return ApiResponse.success(
                    recordService.generateRecord(courseId, employeeId)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<TrainingRecord> getById(@PathVariable String id) {
        TrainingRecord record = recordService.getRecord(id);
        if (record == null) {
            return ApiResponse.error(404, "培训记录不存在");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/by-course-and-employee")
    public ApiResponse<TrainingRecord> getByCourseAndEmployee(
            @RequestParam String courseId,
            @RequestParam String employeeId) {
        TrainingRecord record = recordService.getByCourseAndEmployee(courseId, employeeId);
        if (record == null) {
            return ApiResponse.error(404, "未找到培训记录");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<TrainingRecord>> getByEmployee(@PathVariable String employeeId) {
        return ApiResponse.success(recordService.getRecordsByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/year/{year}")
    public ApiResponse<List<TrainingRecord>> getByEmployeeAndYear(
            @PathVariable String employeeId,
            @PathVariable int year) {
        return ApiResponse.success(recordService.getRecordsByEmployeeAndYear(employeeId, year));
    }

    @GetMapping("/employee/{employeeId}/annual-summary/{year}")
    public ApiResponse<AnnualHoursSummary> getAnnualSummary(
            @PathVariable String employeeId,
            @PathVariable int year) {
        try {
            return ApiResponse.success(recordService.getAnnualHoursSummary(employeeId, year));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<TrainingRecord>> getByCourse(@PathVariable String courseId) {
        return ApiResponse.success(recordService.getRecordsByCourse(courseId));
    }

    @GetMapping("/department/{departmentId}")
    public ApiResponse<List<TrainingRecord>> getByDepartment(@PathVariable String departmentId) {
        return ApiResponse.success(recordService.getRecordsByDepartment(departmentId));
    }

    @GetMapping("/department/{departmentId}/year/{year}")
    public ApiResponse<List<TrainingRecord>> getByDepartmentAndYear(
            @PathVariable String departmentId,
            @PathVariable int year) {
        return ApiResponse.success(recordService.getRecordsByDepartmentAndYear(departmentId, year));
    }

    @GetMapping("/employee/{employeeId}/total-hours/{year}")
    public ApiResponse<Double> getTotalHours(@PathVariable String employeeId, @PathVariable int year) {
        return ApiResponse.success(recordService.getTotalHoursByEmployeeAndYear(employeeId, year));
    }

    @GetMapping("/employee/{employeeId}/external-hours/{year}")
    public ApiResponse<Double> getExternalHours(@PathVariable String employeeId, @PathVariable int year) {
        return ApiResponse.success(recordService.getExternalHoursByEmployeeAndYear(employeeId, year));
    }

    @PutMapping("/{id}/evaluation")
    public ApiResponse<TrainingRecord> updateEvaluation(
            @PathVariable String id,
            @RequestBody Map<String, Double> request) {
        try {
            double score = request.get("score");
            return ApiResponse.success(recordService.updateEvaluation(id, score));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        recordService.deleteRecord(id);
        return ApiResponse.success();
    }
}
