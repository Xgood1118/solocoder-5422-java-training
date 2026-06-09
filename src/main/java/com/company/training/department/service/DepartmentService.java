package com.company.training.department.service;

import com.company.training.department.entity.Department;
import com.company.training.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        if (department.getId() == null || department.getId().isEmpty()) {
            department.setId(UUID.randomUUID().toString());
        }
        return departmentRepository.save(department);
    }

    public Department getDepartment(String id) {
        return departmentRepository.findById(id);
    }

    public Collection<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department updateDepartment(String id, Department department) {
        Department existing = departmentRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("部门不存在: " + id);
        }
        department.setId(id);
        return departmentRepository.save(department);
    }

    public void deleteDepartment(String id) {
        departmentRepository.deleteById(id);
    }
}
