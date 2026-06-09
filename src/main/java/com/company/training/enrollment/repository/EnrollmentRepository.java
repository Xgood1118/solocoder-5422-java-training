package com.company.training.enrollment.repository;

import com.company.training.common.enums.EnrollmentStatus;
import com.company.training.enrollment.entity.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class EnrollmentRepository {

    private final ConcurrentHashMap<String, Enrollment> storage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> waitlistCounters = new ConcurrentHashMap<>();

    public Enrollment save(Enrollment enrollment) {
        storage.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    public Enrollment findById(String id) {
        return storage.get(id);
    }

    public Enrollment findByCourseAndEmployee(String courseId, String employeeId) {
        return storage.values().stream()
                .filter(e -> courseId.equals(e.getCourseId()) && employeeId.equals(e.getEmployeeId()))
                .filter(e -> e.getStatus() != EnrollmentStatus.CANCELLED)
                .findFirst()
                .orElse(null);
    }

    public List<Enrollment> findByCourseId(String courseId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (courseId.equals(e.getCourseId())) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findByCourseIdAndStatus(String courseId, EnrollmentStatus status) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (courseId.equals(e.getCourseId()) && e.getStatus() == status) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findByEmployeeId(String employeeId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (employeeId.equals(e.getEmployeeId())) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findByEmployeeIdAndStatus(String employeeId, EnrollmentStatus status) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : storage.values()) {
            if (employeeId.equals(e.getEmployeeId()) && e.getStatus() == status) {
                result.add(e);
            }
        }
        return result;
    }

    public long countByCourseIdAndStatus(String courseId, EnrollmentStatus status) {
        return storage.values().stream()
                .filter(e -> courseId.equals(e.getCourseId()) && e.getStatus() == status)
                .count();
    }

    public List<Enrollment> getWaitlistOrdered(String courseId) {
        return storage.values().stream()
                .filter(e -> courseId.equals(e.getCourseId()) && e.getStatus() == EnrollmentStatus.WAITLISTED)
                .sorted(Comparator.comparing(Enrollment::getWaitlistPosition))
                .toList();
    }

    public int getNextWaitlistPosition(String courseId) {
        return waitlistCounters.computeIfAbsent(courseId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void decrementWaitlistPositions(String courseId, int fromPosition) {
        storage.values().stream()
                .filter(e -> courseId.equals(e.getCourseId()) && e.getStatus() == EnrollmentStatus.WAITLISTED)
                .filter(e -> e.getWaitlistPosition() != null && e.getWaitlistPosition() > fromPosition)
                .forEach(e -> e.setWaitlistPosition(e.getWaitlistPosition() - 1));
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
