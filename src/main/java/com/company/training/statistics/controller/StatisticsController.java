package com.company.training.statistics.controller;

import com.company.training.common.response.ApiResponse;
import com.company.training.statistics.dto.CompanyStatistics;
import com.company.training.statistics.dto.DepartmentStatistics;
import com.company.training.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/department/{departmentId}")
    public ApiResponse<DepartmentStatistics> getDepartmentStats(
            @PathVariable String departmentId,
            @RequestParam int year,
            @RequestParam(defaultValue = "0") int quarter) {
        try {
            return ApiResponse.success(
                    statisticsService.getDepartmentStatistics(departmentId, year, quarter));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/company")
    public ApiResponse<CompanyStatistics> getCompanyStats(
            @RequestParam int year,
            @RequestParam(defaultValue = "0") int quarter) {
        return ApiResponse.success(statisticsService.getCompanyStatistics(year, quarter));
    }
}
