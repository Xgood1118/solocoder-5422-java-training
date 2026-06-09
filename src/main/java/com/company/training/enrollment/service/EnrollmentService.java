package com.company.training.enrollment.service;

import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.EnrollmentStatus;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.enrollment.entity.Enrollment;
import com.company.training.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final EmployeeService employeeService;

    public Enrollment enroll(String courseId, String employeeId) {
        Course course = courseService.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在: " + courseId);
        }
        if (course.getStatus() != CourseStatus.PUBLISHED && course.getStatus() != CourseStatus.ONGOING) {
            throw new IllegalStateException("课程未发布，无法报名");
        }

        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        Enrollment existing = enrollmentRepository.findByCourseAndEmployee(courseId, employeeId);
        if (existing != null) {
            throw new IllegalStateException("已经报名过该课程");
        }

        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.ENROLLED);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(UUID.randomUUID().toString());
        enrollment.setCourseId(courseId);
        enrollment.setEmployeeId(employeeId);
        enrollment.setEnrolledAt(LocalDateTime.now());

        if (enrolledCount < course.getCapacity()) {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        } else {
            long waitlistCount = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.WAITLISTED);
            if (course.getWaitlistCapacity() != null && waitlistCount >= course.getWaitlistCapacity()) {
                throw new IllegalStateException("报名已满且候补队列已满");
            }
            enrollment.setStatus(EnrollmentStatus.WAITLISTED);
            enrollment.setWaitlistPosition(enrollmentRepository.getNextWaitlistPosition(courseId));
        }

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment cancelEnrollment(String enrollmentId, String cancelledBy) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        if (enrollment == null) {
            throw new IllegalArgumentException("报名记录不存在: " + enrollmentId);
        }
        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new IllegalStateException("该报名已取消");
        }
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("已完成的培训不能取消报名");
        }

        EnrollmentStatus oldStatus = enrollment.getStatus();
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollment.setCancelledAt(LocalDateTime.now());
        enrollment.setCancelledBy(cancelledBy);
        enrollmentRepository.save(enrollment);

        if (oldStatus == EnrollmentStatus.ENROLLED) {
            promoteFromWaitlist(enrollment.getCourseId());
        } else if (oldStatus == EnrollmentStatus.WAITLISTED) {
            enrollmentRepository.decrementWaitlistPositions(
                    enrollment.getCourseId(),
                    enrollment.getWaitlistPosition()
            );
        }

        return enrollment;
    }

    private void promoteFromWaitlist(String courseId) {
        List<Enrollment> waitlist = enrollmentRepository.getWaitlistOrdered(courseId);
        if (!waitlist.isEmpty()) {
            Enrollment first = waitlist.get(0);
            first.setStatus(EnrollmentStatus.ENROLLED);
            first.setWaitlistPosition(null);
            enrollmentRepository.save(first);
            enrollmentRepository.decrementWaitlistPositions(courseId, 0);
        }
    }

    public Enrollment getEnrollment(String id) {
        return enrollmentRepository.findById(id);
    }

    public Enrollment getEnrollmentByCourseAndEmployee(String courseId, String employeeId) {
        return enrollmentRepository.findByCourseAndEmployee(courseId, employeeId);
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public List<Enrollment> getEnrollmentsByCourseAndStatus(String courseId, EnrollmentStatus status) {
        return enrollmentRepository.findByCourseIdAndStatus(courseId, status);
    }

    public List<Enrollment> getEnrollmentsByEmployee(String employeeId) {
        return enrollmentRepository.findByEmployeeId(employeeId);
    }

    public List<Enrollment> getEnrollmentsByEmployeeAndStatus(String employeeId, EnrollmentStatus status) {
        return enrollmentRepository.findByEmployeeIdAndStatus(employeeId, status);
    }

    public List<Enrollment> getWaitlist(String courseId) {
        return enrollmentRepository.getWaitlistOrdered(courseId);
    }

    public long getEnrolledCount(String courseId) {
        return enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.ENROLLED);
    }

    public long getWaitlistCount(String courseId) {
        return enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.WAITLISTED);
    }

    public Enrollment completeEnrollment(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        if (enrollment == null) {
            throw new IllegalArgumentException("报名记录不存在: " + enrollmentId);
        }
        if (enrollment.getStatus() != EnrollmentStatus.ENROLLED) {
            throw new IllegalStateException("只有已报名状态可以标记为完成");
        }
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        return enrollmentRepository.save(enrollment);
    }
}
