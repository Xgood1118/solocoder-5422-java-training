package com.company.training.attendance.controller;

import com.company.training.attendance.entity.AttendanceRecord;
import com.company.training.attendance.entity.QrCodeToken;
import com.company.training.attendance.service.AttendanceService;
import com.company.training.common.enums.AttendanceStatus;
import com.company.training.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/qr-code/generate")
    public ApiResponse<QrCodeToken> generateQrCode(@RequestBody Map<String, String> request) {
        try {
            String courseId = request.get("courseId");
            String sessionId = request.get("sessionId");
            return ApiResponse.success(attendanceService.generateQrCode(courseId, sessionId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/qr-code/latest/{courseId}")
    public ApiResponse<QrCodeToken> getLatestQrToken(@PathVariable String courseId) {
        QrCodeToken token = attendanceService.getLatestQrToken(courseId);
        if (token == null) {
            return ApiResponse.error(404, "暂无有效的签到码");
        }
        return ApiResponse.success(token);
    }

    @PostMapping("/sign-in/qr-code")
    public ApiResponse<AttendanceRecord> signInWithQrCode(@RequestBody Map<String, Object> request) {
        try {
            String token = (String) request.get("token");
            String employeeId = (String) request.get("employeeId");
            Double latitude = request.get("latitude") != null ? ((Number) request.get("latitude")).doubleValue() : null;
            Double longitude = request.get("longitude") != null ? ((Number) request.get("longitude")).doubleValue() : null;
            return ApiResponse.success(
                    attendanceService.signInWithQrCode(token, employeeId, latitude, longitude)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/sign-in/face")
    public ApiResponse<AttendanceRecord> signInWithFace(@RequestBody Map<String, String> request) {
        try {
            String courseId = request.get("courseId");
            String employeeId = request.get("employeeId");
            String faceResult = request.get("faceRecognitionResult");
            return ApiResponse.success(
                    attendanceService.signInWithFaceRecognition(courseId, employeeId, faceResult)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/sign-in/manual")
    public ApiResponse<AttendanceRecord> manualSignIn(@RequestBody Map<String, String> request) {
        try {
            String courseId = request.get("courseId");
            String employeeId = request.get("employeeId");
            String operatorId = request.get("operatorId");
            return ApiResponse.success(
                    attendanceService.manualSignIn(courseId, employeeId, operatorId)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/absent/{employeeId}")
    public ApiResponse<Void> markAbsent(@PathVariable String courseId, @PathVariable String employeeId) {
        try {
            attendanceService.markAbsent(courseId, employeeId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/records/{id}")
    public ApiResponse<AttendanceRecord> getById(@PathVariable String id) {
        AttendanceRecord record = attendanceService.getAttendanceRecord(id);
        if (record == null) {
            return ApiResponse.error(404, "签到记录不存在");
        }
        return ApiResponse.success(record);
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<AttendanceRecord>> getByCourse(@PathVariable String courseId) {
        return ApiResponse.success(attendanceService.getByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/status/{status}")
    public ApiResponse<List<AttendanceRecord>> getByCourseAndStatus(
            @PathVariable String courseId,
            @PathVariable AttendanceStatus status) {
        return ApiResponse.success(attendanceService.getByCourseAndStatus(courseId, status));
    }

    @GetMapping("/course/{courseId}/stats")
    public ApiResponse<Map<String, Long>> getCourseStats(@PathVariable String courseId) {
        long signedIn = attendanceService.getSignedCount(courseId);
        long absent = attendanceService.getAbsentCount(courseId);
        return ApiResponse.success(Map.of(
                "signedIn", signedIn,
                "absent", absent
        ));
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<AttendanceRecord>> getByEmployee(@PathVariable String employeeId) {
        return ApiResponse.success(attendanceService.getByEmployee(employeeId));
    }

    @GetMapping("/by-course-and-employee")
    public ApiResponse<AttendanceRecord> getByCourseAndEmployee(
            @RequestParam String courseId,
            @RequestParam String employeeId) {
        AttendanceRecord record = attendanceService.getByCourseAndEmployee(courseId, employeeId);
        if (record == null) {
            return ApiResponse.error(404, "未找到签到记录");
        }
        return ApiResponse.success(record);
    }
}
