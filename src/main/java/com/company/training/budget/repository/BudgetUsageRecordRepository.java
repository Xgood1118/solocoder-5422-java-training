package com.company.training.budget.repository;

import com.company.training.budget.entity.BudgetUsageRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BudgetUsageRecordRepository {

    private final ConcurrentHashMap<String, BudgetUsageRecord> storage = new ConcurrentHashMap<>();

    public BudgetUsageRecord save(BudgetUsageRecord record) {
        storage.put(record.getId(), record);
        return record;
    }

    public BudgetUsageRecord findById(String id) {
        return storage.get(id);
    }

    public BudgetUsageRecord findByCourseId(String courseId) {
        return storage.values().stream()
                .filter(r -> courseId.equals(r.getCourseId()))
                .findFirst()
                .orElse(null);
    }

    public List<BudgetUsageRecord> findByDepartmentId(String departmentId) {
        List<BudgetUsageRecord> result = new ArrayList<>();
        for (BudgetUsageRecord r : storage.values()) {
            if (departmentId.equals(r.getDepartmentId())) {
                result.add(r);
            }
        }
        return result;
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
