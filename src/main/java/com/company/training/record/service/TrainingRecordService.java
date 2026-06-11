package com.company.training.record.service;

import com.company.training.attendance.entity.AttendanceRecord;
import com.company.training.attendance.service.AttendanceService;
import com.company.training.common.config.TrainingProperties;
import com.company.training.common.enums.*;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.enrollment.entity.Enrollment;
import com.company.training.enrollment.service.EnrollmentService;
import com.company.training.exam.entity.ExamAttempt;
import com.company.training.exam.service.ExamService;
import com.company.training.record.entity.AnnualHoursSummary;
import com.company.training.record.entity.TrainingRecord;
import com.company.training.record.repository.TrainingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingRecordService {

    private final TrainingRecordRepository recordRepository;
    private final CourseService courseService;
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final ExamService examService;
    private final EnrollmentService enrollmentService;
    private final TrainingProperties trainingProperties;

    public TrainingRecord generateRecord(String courseId, String employeeId) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }

        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        TrainingRecord existing = recordRepository.findByCourseAndEmployee(courseId, employeeId);
        if (existing != null) {
            return existing;
        }

        Enrollment enrollment = enrollmentService.getEnrollmentByCourseAndEmployee(courseId, employeeId);
        if (enrollment == null || enrollment.getStatus() != EnrollmentStatus.ENROLLED) {
            throw new IllegalStateException("员工未报名或未成功报名此课程");
        }

        AttendanceRecord attendance = attendanceService.getByCourseAndEmployee(courseId, employeeId);
        if (attendance == null || attendance.getStatus() != AttendanceStatus.SIGNED_IN) {
            throw new IllegalStateException("员工未完成签到，无法生成培训记录");
        }

        boolean examPassed = true;
        Integer examScore = null;
        String examAttemptId = null;
        if (course.isRequiresExam()) {
            List<ExamAttempt> attempts = examService.getAttemptsByCourseAndEmployee(courseId, employeeId);
            ExamAttempt passedAttempt = attempts.stream()
                    .filter(a -> a.getResult() == ExamResult.PASSED)
                    .findFirst()
                    .orElse(null);
            if (passedAttempt == null) {
                throw new IllegalStateException("员工未通过该课程考试，无法生成培训记录");
            }
            examPassed = true;
            examScore = passedAttempt.getScore();
            examAttemptId = passedAttempt.getId();
        }

        boolean passed = examPassed;

        TrainingRecord record = new TrainingRecord();
        record.setId(UUID.randomUUID().toString());
        record.setEmployeeId(employeeId);
        record.setCourseId(courseId);
        record.setCourseName(course.getName());
        record.setCourseType(course.getCourseType());
        record.setDepartmentId(employee.getDepartmentId());
        record.setEnrollmentId(enrollment.getId());
        record.setAttendanceId(attendance.getId());
        record.setExamAttemptId(examAttemptId);
        record.setCreditHours(passed && course.getCreditHours() != null ? course.getCreditHours() : 0.0);
        record.setExamScore(examScore);
        record.setPassed(passed);
        record.setCompletedAt(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());

        return recordRepository.save(record);
    }

    public TrainingRecord getRecord(String id) {
        return recordRepository.findById(id);
    }

    public TrainingRecord getByCourseAndEmployee(String courseId, String employeeId) {
        return recordRepository.findByCourseAndEmployee(courseId, employeeId);
    }

    public List<TrainingRecord> getRecordsByEmployee(String employeeId) {
        return recordRepository.findByEmployeeId(employeeId);
    }

    public List<TrainingRecord> getRecordsByEmployeeAndYear(String employeeId, int year) {
        return recordRepository.findByEmployeeIdAndYear(employeeId, year);
    }

    public List<TrainingRecord> getRecordsByCourse(String courseId) {
        return recordRepository.findByCourseId(courseId);
    }

    public List<TrainingRecord> getRecordsByDepartment(String departmentId) {
        return recordRepository.findByDepartmentId(departmentId);
    }

    public List<TrainingRecord> getRecordsByDepartmentAndYear(String departmentId, int year) {
        return recordRepository.findByDepartmentIdAndYear(departmentId, year);
    }

    public AnnualHoursSummary getAnnualHoursSummary(String employeeId, int year) {
        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        List<TrainingRecord> records = recordRepository.findByEmployeeIdAndYear(employeeId, year);

        double totalHours = 0;
        double internalHours = 0;
        double externalHours = 0;
        double onlineHours = 0;
        double bookClubHours = 0;

        for (TrainingRecord record : records) {
            if (record.isPassed() && record.getCreditHours() != null) {
                double hours = record.getCreditHours();
                totalHours += hours;
                switch (record.getCourseType()) {
                    case INTERNAL_TECH, INTERNAL_SOFT_SKILL -> internalHours += hours;
                    case EXTERNAL -> externalHours += hours;
                    case ONLINE -> onlineHours += hours;
                    case BOOK_CLUB -> bookClubHours += hours;
                }
            }
        }

        int requiredHours = getRequiredHours(employee.getPosition());
        int requiredExternalHours = trainingProperties.getAnnualHours().getMinExternalHours();
        double completionRate = requiredHours > 0 ? (totalHours / requiredHours) * 100 : 0;
        boolean meetsRequirement = totalHours >= requiredHours;
        boolean meetsExternalRequirement = externalHours >= requiredExternalHours;

        AnnualHoursSummary summary = new AnnualHoursSummary();
        summary.setEmployeeId(employeeId);
        summary.setEmployeeName(employee.getName());
        summary.setDepartmentId(employee.getDepartmentId());
        summary.setPositionType(employee.getPosition().name());
        summary.setYear(year);
        summary.setTotalHours(totalHours);
        summary.setInternalHours(internalHours);
        summary.setExternalHours(externalHours);
        summary.setOnlineHours(onlineHours);
        summary.setBookClubHours(bookClubHours);
        summary.setRequiredHours(requiredHours);
        summary.setRequiredExternalHours(requiredExternalHours);
        summary.setCompletionRate(Math.round(completionRate * 100) / 100.0);
        summary.setMeetsRequirement(meetsRequirement);
        summary.setMeetsExternalRequirement(meetsExternalRequirement);

        return summary;
    }

    private int getRequiredHours(PositionType position) {
        return switch (position) {
            case MANAGEMENT -> trainingProperties.getAnnualHours().getManagement();
            case RND -> trainingProperties.getAnnualHours().getRnd();
            case SALES -> trainingProperties.getAnnualHours().getSales();
        };
    }

    public double getTotalHoursByEmployeeAndYear(String employeeId, int year) {
        return recordRepository.sumTotalHoursByEmployeeAndYear(employeeId, year);
    }

    public double getExternalHoursByEmployeeAndYear(String employeeId, int year) {
        return recordRepository.sumHoursByEmployeeAndYearAndType(employeeId, year, CourseType.EXTERNAL);
    }

    public TrainingRecord updateEvaluation(String recordId, double evaluationScore) {
        TrainingRecord record = recordRepository.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("培训记录不存在: " + recordId);
        }
        record.setEvaluationScore(evaluationScore);
        return recordRepository.save(record);
    }

    public void deleteRecord(String id) {
        recordRepository.deleteById(id);
    }
}
