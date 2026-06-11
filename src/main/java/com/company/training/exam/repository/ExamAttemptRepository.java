package com.company.training.exam.repository;

import com.company.training.common.enums.ExamResult;
import com.company.training.exam.entity.ExamAttempt;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExamAttemptRepository {

    private final ConcurrentHashMap<String, ExamAttempt> storage = new ConcurrentHashMap<>();

    public ExamAttempt save(ExamAttempt attempt) {
        storage.put(attempt.getId(), attempt);
        return attempt;
    }

    public ExamAttempt findById(String id) {
        return storage.get(id);
    }

    public List<ExamAttempt> findByExamId(String examId) {
        List<ExamAttempt> result = new ArrayList<>();
        for (ExamAttempt a : storage.values()) {
            if (examId.equals(a.getExamId())) {
                result.add(a);
            }
        }
        return result;
    }

    public List<ExamAttempt> findByExamIdAndEmployeeId(String examId, String employeeId) {
        return storage.values().stream()
                .filter(a -> examId.equals(a.getExamId()) && employeeId.equals(a.getEmployeeId()))
                .sorted(Comparator.comparingInt(ExamAttempt::getAttemptNumber))
                .toList();
    }

    public List<ExamAttempt> findByCourseIdAndEmployeeId(String courseId, String employeeId) {
        return storage.values().stream()
                .filter(a -> courseId.equals(a.getCourseId()) && employeeId.equals(a.getEmployeeId()))
                .sorted(Comparator.comparingInt(ExamAttempt::getAttemptNumber))
                .toList();
    }

    public List<ExamAttempt> findByEmployeeId(String employeeId) {
        List<ExamAttempt> result = new ArrayList<>();
        for (ExamAttempt a : storage.values()) {
            if (employeeId.equals(a.getEmployeeId())) {
                result.add(a);
            }
        }
        return result;
    }

    public int countAttempts(String examId, String employeeId) {
        return (int) storage.values().stream()
                .filter(a -> examId.equals(a.getExamId()) && employeeId.equals(a.getEmployeeId()))
                .filter(a -> a.getResult() != null && a.getResult() != ExamResult.NOT_TAKEN)
                .count();
    }

    public ExamAttempt findLatestAttempt(String examId, String employeeId) {
        return storage.values().stream()
                .filter(a -> examId.equals(a.getExamId()) && employeeId.equals(a.getEmployeeId()))
                .max(Comparator.comparingInt(ExamAttempt::getAttemptNumber))
                .orElse(null);
    }

    public ExamAttempt findUnfinishedAttempt(String examId, String employeeId) {
        return storage.values().stream()
                .filter(a -> examId.equals(a.getExamId()) && employeeId.equals(a.getEmployeeId()))
                .filter(a -> a.getResult() == ExamResult.NOT_TAKEN)
                .findFirst()
                .orElse(null);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
