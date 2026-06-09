package com.company.training.employee.controller;

import com.company.training.common.response.ApiResponse;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ApiResponse<Employee> create(@RequestBody Employee employee) {
        return ApiResponse.success(employeeService.createEmployee(employee));
    }

    @GetMapping("/{id}")
    public ApiResponse<Employee> getById(@PathVariable String id) {
        Employee emp = employeeService.getEmployee(id);
        if (emp == null) {
            return ApiResponse.error(404, "员工不存在");
        }
        return ApiResponse.success(emp);
    }

    @GetMapping
    public ApiResponse<Collection<Employee>> getAll() {
        return ApiResponse.success(employeeService.getAllEmployees());
    }

    @GetMapping("/by-no/{employeeNo}")
    public ApiResponse<Employee> getByNo(@PathVariable String employeeNo) {
        Employee emp = employeeService.getEmployeeByNo(employeeNo);
        if (emp == null) {
            return ApiResponse.error(404, "员工不存在");
        }
        return ApiResponse.success(emp);
    }

    @GetMapping("/by-department/{departmentId}")
    public ApiResponse<List<Employee>> getByDepartment(@PathVariable String departmentId) {
        return ApiResponse.success(employeeService.getEmployeesByDepartment(departmentId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Employee> update(@PathVariable String id, @RequestBody Employee employee) {
        try {
            return ApiResponse.success(employeeService.updateEmployee(id, employee));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ApiResponse.success();
    }
}
