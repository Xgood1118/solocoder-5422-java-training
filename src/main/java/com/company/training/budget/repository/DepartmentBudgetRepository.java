package com.company.training.budget.repository;

import com.company.training.budget.entity.DepartmentBudget;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DepartmentBudgetRepository {

    private final ConcurrentHashMap<String, DepartmentBudget> storage = new ConcurrentHashMap<>();

    public DepartmentBudget save(DepartmentBudget budget) {
        storage.put(budget.getId(), budget);
        return budget;
    }

    public DepartmentBudget findById(String id) {
        return storage.get(id);
    }

    public DepartmentBudget findByDepartmentIdAndYear(String departmentId, Year fiscalYear) {
        return storage.values().stream()
                .filter(b -> departmentId.equals(b.getDepartmentId())
                        && fiscalYear.equals(b.getFiscalYear()))
                .findFirst()
                .orElse(null);
    }

    public List<DepartmentBudget> findByYear(Year fiscalYear) {
        List<DepartmentBudget> result = new ArrayList<>();
        for (DepartmentBudget b : storage.values()) {
            if (fiscalYear.equals(b.getFiscalYear())) {
                result.add(b);
            }
        }
        return result;
    }

    public List<DepartmentBudget> findAll() {
        return new ArrayList<>(storage.values());
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
