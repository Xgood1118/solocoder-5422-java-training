package com.company.training.attendance.repository;

import com.company.training.attendance.entity.AttendanceRecord;
import com.company.training.common.enums.AttendanceStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AttendanceRepository {

    private final ConcurrentHashMap<String, AttendanceRecord> storage = new ConcurrentHashMap<>();

    public AttendanceRecord save(AttendanceRecord record) {
        storage.put(record.getId(), record);
        return record;
    }

    public AttendanceRecord findById(String id) {
        return storage.get(id);
    }

    public AttendanceRecord findByCourseAndEmployee(String courseId, String employeeId) {
        return storage.values().stream()
                .filter(r -> courseId.equals(r.getCourseId()) && employeeId.equals(r.getEmployeeId()))
                .findFirst()
                .orElse(null);
    }

    public AttendanceRecord findByEnrollmentId(String enrollmentId) {
        return storage.values().stream()
                .filter(r -> enrollmentId.equals(r.getEnrollmentId()))
                .findFirst()
                .orElse(null);
    }

    public List<AttendanceRecord> findByCourseId(String courseId) {
        List<AttendanceRecord> result = new ArrayList<>();
        for (AttendanceRecord r : storage.values()) {
            if (courseId.equals(r.getCourseId())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<AttendanceRecord> findByCourseIdAndStatus(String courseId, AttendanceStatus status) {
        List<AttendanceRecord> result = new ArrayList<>();
        for (AttendanceRecord r : storage.values()) {
            if (courseId.equals(r.getCourseId()) && r.getStatus() == status) {
                result.add(r);
            }
        }
        return result;
    }

    public List<AttendanceRecord> findByEmployeeId(String employeeId) {
        List<AttendanceRecord> result = new ArrayList<>();
        for (AttendanceRecord r : storage.values()) {
            if (employeeId.equals(r.getEmployeeId())) {
                result.add(r);
            }
        }
        return result;
    }

    public long countByCourseIdAndStatus(String courseId, AttendanceStatus status) {
        return storage.values().stream()
                .filter(r -> courseId.equals(r.getCourseId()) && r.getStatus() == status)
                .count();
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
