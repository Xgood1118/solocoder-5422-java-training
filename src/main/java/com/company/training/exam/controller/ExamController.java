package com.company.training.exam.controller;

import com.company.training.common.response.ApiResponse;
import com.company.training.exam.entity.Exam;
import com.company.training.exam.entity.ExamAttempt;
import com.company.training.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ApiResponse<Exam> create(@RequestBody Exam exam) {
        return ApiResponse.success(examService.createExam(exam));
    }

    @GetMapping("/{id}")
    public ApiResponse<Exam> getById(@PathVariable String id) {
        Exam exam = examService.getExam(id);
        if (exam == null) {
            return ApiResponse.error(404, "考试不存在");
        }
        return ApiResponse.success(exam);
    }

    @GetMapping("/by-course/{courseId}")
    public ApiResponse<Exam> getByCourse(@PathVariable String courseId) {
        Exam exam = examService.getExamByCourse(courseId);
        if (exam == null) {
            return ApiResponse.error(404, "该课程暂无考试");
        }
        return ApiResponse.success(exam);
    }

    @GetMapping
    public ApiResponse<List<Exam>> getAll() {
        return ApiResponse.success(examService.getAllExams());
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Exam> publish(@PathVariable String id) {
        try {
            return ApiResponse.success(examService.publishExam(id));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Exam> update(@PathVariable String id, @RequestBody Exam exam) {
        try {
            return ApiResponse.success(examService.updateExam(id, exam));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        examService.deleteExam(id);
        return ApiResponse.success();
    }

    @PostMapping("/{examId}/start")
    public ApiResponse<ExamAttempt> startExam(@PathVariable String examId,
                                            @RequestBody Map<String, String> request) {
        try {
            String employeeId = request.get("employeeId");
            return ApiResponse.success(examService.startExam(examId, employeeId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ApiResponse<ExamAttempt> submitExam(@PathVariable String attemptId,
                                              @RequestBody Map<String, String> answers) {
        try {
            return ApiResponse.success(examService.submitExam(attemptId, answers));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/{examId}/can-retake")
    public ApiResponse<Boolean> canRetake(@PathVariable String examId,
                                        @RequestParam String employeeId) {
        return ApiResponse.success(examService.canRetake(examId, employeeId));
    }

    @GetMapping("/attempts/{id}")
    public ApiResponse<ExamAttempt> getAttempt(@PathVariable String id) {
        ExamAttempt attempt = examService.getAttempt(id);
        if (attempt == null) {
            return ApiResponse.error(404, "考试记录不存在");
        }
        return ApiResponse.success(attempt);
    }

    @GetMapping("/{examId}/attempts")
    public ApiResponse<List<ExamAttempt>> getAttemptsByExam(@PathVariable String examId) {
        return ApiResponse.success(examService.getAttemptsByExam(examId));
    }

    @GetMapping("/{examId}/attempts/employee/{employeeId}")
    public ApiResponse<List<ExamAttempt>> getAttemptsByExamAndEmployee(
            @PathVariable String examId,
            @PathVariable String employeeId) {
        return ApiResponse.success(examService.getAttemptsByExamAndEmployee(examId, employeeId));
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<ExamAttempt>> getAttemptsByEmployee(@PathVariable String employeeId) {
        return ApiResponse.success(examService.getAttemptsByEmployee(employeeId));
    }

    @GetMapping("/{examId}/attempts/latest")
    public ApiResponse<ExamAttempt> getLatestAttempt(@PathVariable String examId,
                                                   @RequestParam String employeeId) {
        ExamAttempt attempt = examService.getLatestAttempt(examId, employeeId);
        if (attempt == null) {
            return ApiResponse.error(404, "暂无考试记录");
        }
        return ApiResponse.success(attempt);
    }

    @GetMapping("/by-course/{courseId}/passed")
    public ApiResponse<Boolean> hasPassed(@PathVariable String courseId,
                                        @RequestParam String employeeId) {
        return ApiResponse.success(examService.hasPassed(courseId, employeeId));
    }
}
