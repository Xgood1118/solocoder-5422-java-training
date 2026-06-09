package com.company.training.record.repository;

import com.company.training.common.enums.CourseType;
import com.company.training.record.entity.TrainingRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainingRecordRepository {

    private final ConcurrentHashMap<String, TrainingRecord> storage = new ConcurrentHashMap<>();

    public TrainingRecord save(TrainingRecord record) {
        storage.put(record.getId(), record);
        return record;
    }

    public TrainingRecord findById(String id) {
        return storage.get(id);
    }

    public TrainingRecord findByCourseAndEmployee(String courseId, String employeeId) {
        return storage.values().stream()
                .filter(r -> courseId.equals(r.getCourseId()) && employeeId.equals(r.getEmployeeId()))
                .findFirst()
                .orElse(null);
    }

    public List<TrainingRecord> findByEmployeeId(String employeeId) {
        List<TrainingRecord> result = new ArrayList<>();
        for (TrainingRecord r : storage.values()) {
            if (employeeId.equals(r.getEmployeeId())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<TrainingRecord> findByEmployeeIdAndYear(String employeeId, int year) {
        LocalDateTime startOfYear = Year.of(year).atDay(1).atStartOfDay();
        LocalDateTime endOfYear = Year.of(year).atMonth(12).atEndOfMonth().atTime(23, 59, 59);
        List<TrainingRecord> result = new ArrayList<>();
        for (TrainingRecord r : storage.values()) {
            if (employeeId.equals(r.getEmployeeId()) && r.getCompletedAt() != null
                    && !r.getCompletedAt().isBefore(startOfYear)
                    && !r.getCompletedAt().isAfter(endOfYear)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<TrainingRecord> findByCourseId(String courseId) {
        List<TrainingRecord> result = new ArrayList<>();
        for (TrainingRecord r : storage.values()) {
            if (courseId.equals(r.getCourseId())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<TrainingRecord> findByDepartmentId(String departmentId) {
        List<TrainingRecord> result = new ArrayList<>();
        for (TrainingRecord r : storage.values()) {
            if (departmentId.equals(r.getDepartmentId())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<TrainingRecord> findByDepartmentIdAndYear(String departmentId, int year) {
        LocalDateTime startOfYear = Year.of(year).atDay(1).atStartOfDay();
        LocalDateTime endOfYear = Year.of(year).atMonth(12).atEndOfMonth().atTime(23, 59, 59);
        List<TrainingRecord> result = new ArrayList<>();
        for (TrainingRecord r : storage.values()) {
            if (departmentId.equals(r.getDepartmentId()) && r.getCompletedAt() != null
                    && !r.getCompletedAt().isBefore(startOfYear)
                    && !r.getCompletedAt().isAfter(endOfYear)) {
                result.add(r);
            }
        }
        return result;
    }

    public double sumHoursByEmployeeAndYearAndType(String employeeId, int year, CourseType type) {
        return findByEmployeeIdAndYear(employeeId, year).stream()
                .filter(r -> r.getCourseType() == type && r.isPassed())
                .mapToDouble(TrainingRecord::getCreditHours)
                .sum();
    }

    public double sumTotalHoursByEmployeeAndYear(String employeeId, int year) {
        return findByEmployeeIdAndYear(employeeId, year).stream()
                .filter(TrainingRecord::isPassed)
                .mapToDouble(TrainingRecord::getCreditHours)
                .sum();
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
