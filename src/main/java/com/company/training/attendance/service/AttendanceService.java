package com.company.training.attendance.service;

import com.company.training.attendance.entity.AttendanceRecord;
import com.company.training.attendance.entity.QrCodeToken;
import com.company.training.attendance.repository.AttendanceRepository;
import com.company.training.attendance.repository.QrCodeTokenRepository;
import com.company.training.common.config.TrainingProperties;
import com.company.training.common.enums.AttendanceStatus;
import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.CourseType;
import com.company.training.common.enums.SignInMethod;
import com.company.training.common.util.GpsUtil;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.enrollment.entity.Enrollment;
import com.company.training.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final QrCodeTokenRepository qrCodeTokenRepository;
    private final CourseService courseService;
    private final EmployeeService employeeService;
    private final EnrollmentService enrollmentService;
    private final TrainingProperties trainingProperties;

    public QrCodeToken generateQrCode(String courseId, String sessionId) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getStatus() != CourseStatus.ONGOING && course.getStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("课程未开始，无法生成签到码");
        }
        if (course.getSignInMethod() != SignInMethod.QR_CODE) {
            throw new IllegalStateException("该课程不支持扫码签到");
        }

        QrCodeToken token = new QrCodeToken();
        token.setId(UUID.randomUUID().toString());
        token.setCourseId(courseId);
        token.setSessionId(sessionId);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(trainingProperties.getQrCode().getValidMinutes()));
        token.setUsed(false);

        return qrCodeTokenRepository.save(token);
    }

    public AttendanceRecord signInWithQrCode(String token, String employeeId,
                                             Double latitude, Double longitude) {
        QrCodeToken qrToken = qrCodeTokenRepository.findByToken(token);
        if (qrToken == null) {
            throw new IllegalArgumentException("无效的签到码");
        }
        if (qrToken.isUsed()) {
            throw new IllegalStateException("签到码已被使用");
        }
        if (qrToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("签到码已过期");
        }

        Course course = courseService.getCourse(qrToken.getCourseId());
        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        Enrollment enrollment = enrollmentService.getEnrollmentByCourseAndEmployee(
                qrToken.getCourseId(), employeeId);
        if (enrollment == null) {
            throw new IllegalStateException("您未报名此课程");
        }

        AttendanceRecord existing = attendanceRepository.findByCourseAndEmployee(
                qrToken.getCourseId(), employeeId);
        if (existing != null && existing.getStatus() == AttendanceStatus.SIGNED_IN) {
            throw new IllegalStateException("您已完成签到");
        }

        boolean gpsValid = true;
        String gpsResult = "无需GPS校验";
        if (course.getCourseType() != CourseType.EXTERNAL && course.getCourseType() != CourseType.ONLINE) {
            if (latitude == null || longitude == null) {
                throw new IllegalArgumentException("缺少GPS位置信息");
            }
            TrainingProperties.GpsConfig gpsConfig = trainingProperties.getCompanyGps();
            double distance = GpsUtil.calculateDistance(
                    latitude, longitude,
                    gpsConfig.getLatitude(), gpsConfig.getLongitude()
            );
            gpsValid = distance <= gpsConfig.getRadiusMeters();
            gpsResult = String.format("距离公司%.2f米，%s", distance, gpsValid ? "校验通过" : "超出范围");
            if (!gpsValid) {
                throw new IllegalStateException("GPS校验失败：" + gpsResult);
            }
        }

        qrToken.setUsed(true);
        qrToken.setUsedByEmployeeId(employeeId);
        qrCodeTokenRepository.save(qrToken);

        AttendanceRecord record;
        if (existing != null) {
            record = existing;
        } else {
            record = new AttendanceRecord();
            record.setId(UUID.randomUUID().toString());
        }
        record.setCourseId(qrToken.getCourseId());
        record.setEmployeeId(employeeId);
        record.setEnrollmentId(enrollment.getId());
        record.setStatus(AttendanceStatus.SIGNED_IN);
        record.setSignInMethod(SignInMethod.QR_CODE);
        record.setSignInTime(LocalDateTime.now());
        record.setSignInLatitude(latitude);
        record.setSignInLongitude(longitude);
        record.setGpsValidationResult(gpsResult);
        record.setQrTokenId(qrToken.getId());
        record.setSessionId(qrToken.getSessionId());

        return attendanceRepository.save(record);
    }

    public AttendanceRecord signInWithFaceRecognition(String courseId, String employeeId,
                                                       String faceRecognitionResult) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getSignInMethod() != SignInMethod.FACE_RECOGNITION) {
            throw new IllegalStateException("该课程不支持人脸识别签到");
        }

        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        Enrollment enrollment = enrollmentService.getEnrollmentByCourseAndEmployee(courseId, employeeId);
        if (enrollment == null) {
            throw new IllegalStateException("您未报名此课程");
        }

        AttendanceRecord existing = attendanceRepository.findByCourseAndEmployee(courseId, employeeId);
        if (existing != null && existing.getStatus() == AttendanceStatus.SIGNED_IN) {
            throw new IllegalStateException("您已完成签到");
        }

        if (faceRecognitionResult == null || !faceRecognitionResult.startsWith("MATCH_")) {
            throw new IllegalStateException("人脸识别未通过");
        }

        AttendanceRecord record;
        if (existing != null) {
            record = existing;
        } else {
            record = new AttendanceRecord();
            record.setId(UUID.randomUUID().toString());
        }
        record.setCourseId(courseId);
        record.setEmployeeId(employeeId);
        record.setEnrollmentId(enrollment.getId());
        record.setStatus(AttendanceStatus.SIGNED_IN);
        record.setSignInMethod(SignInMethod.FACE_RECOGNITION);
        record.setSignInTime(LocalDateTime.now());
        record.setFaceRecognitionResult(faceRecognitionResult);

        return attendanceRepository.save(record);
    }

    public AttendanceRecord manualSignIn(String courseId, String employeeId, String operatorId) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }

        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        Enrollment enrollment = enrollmentService.getEnrollmentByCourseAndEmployee(courseId, employeeId);
        if (enrollment == null) {
            throw new IllegalStateException("该员工未报名此课程");
        }

        AttendanceRecord existing = attendanceRepository.findByCourseAndEmployee(courseId, employeeId);
        if (existing != null && existing.getStatus() == AttendanceStatus.SIGNED_IN) {
            throw new IllegalStateException("该员工已完成签到");
        }

        AttendanceRecord record;
        if (existing != null) {
            record = existing;
        } else {
            record = new AttendanceRecord();
            record.setId(UUID.randomUUID().toString());
        }
        record.setCourseId(courseId);
        record.setEmployeeId(employeeId);
        record.setEnrollmentId(enrollment.getId());
        record.setStatus(AttendanceStatus.SIGNED_IN);
        record.setSignInMethod(SignInMethod.MANUAL);
        record.setSignInTime(LocalDateTime.now());

        return attendanceRepository.save(record);
    }

    public void markAbsent(String courseId, String employeeId) {
        AttendanceRecord record = attendanceRepository.findByCourseAndEmployee(courseId, employeeId);
        if (record == null) {
            throw new IllegalArgumentException("未找到该员工的签到记录");
        }
        record.setStatus(AttendanceStatus.ABSENT);
        attendanceRepository.save(record);
    }

    public AttendanceRecord getAttendanceRecord(String id) {
        return attendanceRepository.findById(id);
    }

    public AttendanceRecord getByCourseAndEmployee(String courseId, String employeeId) {
        return attendanceRepository.findByCourseAndEmployee(courseId, employeeId);
    }

    public List<AttendanceRecord> getByCourse(String courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    public List<AttendanceRecord> getByCourseAndStatus(String courseId, AttendanceStatus status) {
        return attendanceRepository.findByCourseIdAndStatus(courseId, status);
    }

    public List<AttendanceRecord> getByEmployee(String employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public long getSignedCount(String courseId) {
        return attendanceRepository.countByCourseIdAndStatus(courseId, AttendanceStatus.SIGNED_IN);
    }

    public long getAbsentCount(String courseId) {
        return attendanceRepository.countByCourseIdAndStatus(courseId, AttendanceStatus.ABSENT);
    }

    public QrCodeToken getLatestQrToken(String courseId) {
        return qrCodeTokenRepository.findLatestValidByCourseId(courseId);
    }
}
