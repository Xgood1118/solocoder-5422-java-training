package com.company.training.employee.service;

import com.company.training.employee.entity.Employee;
import com.company.training.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        if (employee.getId() == null || employee.getId().isEmpty()) {
            employee.setId(UUID.randomUUID().toString());
        }
        return employeeRepository.save(employee);
    }

    public Employee getEmployee(String id) {
        return employeeRepository.findById(id);
    }

    public Employee getEmployeeByNo(String employeeNo) {
        return employeeRepository.findByEmployeeNo(employeeNo);
    }

    public Collection<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByDepartment(String departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public Employee updateEmployee(String id, Employee employee) {
        Employee existing = employeeRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("员工不存在: " + id);
        }
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }
}
