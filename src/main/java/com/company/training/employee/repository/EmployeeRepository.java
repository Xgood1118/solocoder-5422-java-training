package com.company.training.employee.repository;

import com.company.training.employee.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmployeeRepository {

    private final ConcurrentHashMap<String, Employee> storage = new ConcurrentHashMap<>();

    public Employee save(Employee employee) {
        storage.put(employee.getId(), employee);
        return employee;
    }

    public Employee findById(String id) {
        return storage.get(id);
    }

    public Employee findByEmployeeNo(String employeeNo) {
        return storage.values().stream()
                .filter(e -> employeeNo.equals(e.getEmployeeNo()))
                .findFirst()
                .orElse(null);
    }

    public Collection<Employee> findAll() {
        return storage.values();
    }

    public List<Employee> findByDepartmentId(String departmentId) {
        List<Employee> result = new ArrayList<>();
        for (Employee e : storage.values()) {
            if (departmentId.equals(e.getDepartmentId())) {
                result.add(e);
            }
        }
        return result;
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
