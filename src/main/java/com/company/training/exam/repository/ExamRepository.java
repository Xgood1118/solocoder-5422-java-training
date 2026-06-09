package com.company.training.exam.repository;

import com.company.training.exam.entity.Exam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ExamRepository {

    private final ConcurrentHashMap<String, Exam> storage = new ConcurrentHashMap<>();

    public Exam save(Exam exam) {
        storage.put(exam.getId(), exam);
        return exam;
    }

    public Exam findById(String id) {
        return storage.get(id);
    }

    public Exam findByCourseId(String courseId) {
        return storage.values().stream()
                .filter(e -> courseId.equals(e.getCourseId()))
                .findFirst()
                .orElse(null);
    }

    public List<Exam> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
