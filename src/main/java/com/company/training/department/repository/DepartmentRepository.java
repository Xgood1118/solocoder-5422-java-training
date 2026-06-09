package com.company.training.department.repository;

import com.company.training.department.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DepartmentRepository {

    private final ConcurrentHashMap<String, Department> storage = new ConcurrentHashMap<>();

    public Department save(Department department) {
        storage.put(department.getId(), department);
        return department;
    }

    public Department findById(String id) {
        return storage.get(id);
    }

    public Collection<Department> findAll() {
        return storage.values();
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
