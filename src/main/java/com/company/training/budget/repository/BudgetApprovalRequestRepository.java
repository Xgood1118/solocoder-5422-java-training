package com.company.training.budget.repository;

import com.company.training.budget.entity.BudgetApprovalRequest;
import com.company.training.common.enums.BudgetApprovalStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BudgetApprovalRequestRepository {

    private final ConcurrentHashMap<String, BudgetApprovalRequest> storage = new ConcurrentHashMap<>();

    public BudgetApprovalRequest save(BudgetApprovalRequest request) {
        storage.put(request.getId(), request);
        return request;
    }

    public BudgetApprovalRequest findById(String id) {
        return storage.get(id);
    }

    public BudgetApprovalRequest findByCourseId(String courseId) {
        return storage.values().stream()
                .filter(r -> courseId.equals(r.getCourseId()))
                .findFirst()
                .orElse(null);
    }

    public List<BudgetApprovalRequest> findByDepartmentId(String departmentId) {
        List<BudgetApprovalRequest> result = new ArrayList<>();
        for (BudgetApprovalRequest r : storage.values()) {
            if (departmentId.equals(r.getDepartmentId())) {
                result.add(r);
            }
        }
        return result;
    }

    public List<BudgetApprovalRequest> findByStatus(BudgetApprovalStatus status) {
        List<BudgetApprovalRequest> result = new ArrayList<>();
        for (BudgetApprovalRequest r : storage.values()) {
            if (r.getStatus() == status) {
                result.add(r);
            }
        }
        return result;
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
