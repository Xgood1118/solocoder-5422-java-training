package com.company.training.department.controller;

import com.company.training.common.response.ApiResponse;
import com.company.training.department.entity.Department;
import com.company.training.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ApiResponse<Department> create(@RequestBody Department department) {
        return ApiResponse.success(departmentService.createDepartment(department));
    }

    @GetMapping("/{id}")
    public ApiResponse<Department> getById(@PathVariable String id) {
        Department dept = departmentService.getDepartment(id);
        if (dept == null) {
            return ApiResponse.error(404, "部门不存在");
        }
        return ApiResponse.success(dept);
    }

    @GetMapping
    public ApiResponse<Collection<Department>> getAll() {
        return ApiResponse.success(departmentService.getAllDepartments());
    }

    @PutMapping("/{id}")
    public ApiResponse<Department> update(@PathVariable String id, @RequestBody Department department) {
        try {
            return ApiResponse.success(departmentService.updateDepartment(id, department));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        return ApiResponse.success();
    }
}
