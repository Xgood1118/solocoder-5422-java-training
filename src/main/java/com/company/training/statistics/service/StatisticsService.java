package com.company.training.statistics.service;

import com.company.training.budget.entity.DepartmentBudget;
import com.company.training.budget.service.BudgetService;
import com.company.training.common.enums.CourseStatus;
import com.company.training.common.enums.CourseType;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.department.entity.Department;
import com.company.training.department.service.DepartmentService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.enrollment.entity.Enrollment;
import com.company.training.enrollment.service.EnrollmentService;
import com.company.training.record.entity.TrainingRecord;
import com.company.training.record.service.TrainingRecordService;
import com.company.training.statistics.dto.CompanyStatistics;
import com.company.training.statistics.dto.DepartmentStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final TrainingRecordService recordService;
    private final BudgetService budgetService;

    public DepartmentStatistics getDepartmentStatistics(String departmentId, int year, int quarter) {
        Department dept = departmentService.getDepartment(departmentId);
        if (dept == null) {
            throw new IllegalArgumentException("部门不存在: " + departmentId);
        }

        List<Course> allCourses = courseService.getCoursesByDepartment(departmentId);
        List<Course> periodCourses = filterCoursesByPeriod(allCourses, year, quarter);

        int totalCourses = periodCourses.size();
        int completedCourses = (int) periodCourses.stream()
                .filter(c -> c.getStatus() == CourseStatus.COMPLETED).count();
        int ongoingCourses = (int) periodCourses.stream()
                .filter(c -> c.getStatus() == CourseStatus.ONGOING).count();

        Set<String> uniqueParticipants = new HashSet<>();
        int totalEnrollments = 0;
        double totalAttendanceRate = 0;
        double totalPassRate = 0;
        int coursesWithExam = 0;
        double totalEvaluation = 0;
        int evaluatedCourses = 0;
        double totalCreditHours = 0;
        Map<String, Integer> coursesByType = new HashMap<>();
        int newCertificates = 0;

        for (Course course : periodCourses) {
            coursesByType.merge(course.getCourseType().name(), 1, Integer::sum);

            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course.getId());
            totalEnrollments += enrollments.size();

            for (Enrollment enrollment : enrollments) {
                uniqueParticipants.add(enrollment.getEmployeeId());
            }

            long enrolledCount = enrollmentService.getEnrolledCount(course.getId());

            List<TrainingRecord> records = recordService.getRecordsByCourse(course.getId());
            long passedCount = records.stream().filter(TrainingRecord::isPassed).count();
            long attendedCount = records.stream()
                    .filter(r -> r.getCreditHours() != null && r.getCreditHours() > 0).count();

            if (enrolledCount > 0) {
                totalAttendanceRate += (double) attendedCount / enrolledCount;
            }

            if (course.isRequiresExam() && enrolledCount > 0) {
                totalPassRate += (double) passedCount / enrolledCount;
                coursesWithExam++;
            }

            OptionalDouble avgEval = records.stream()
                    .filter(r -> r.getEvaluationScore() != null)
                    .mapToDouble(TrainingRecord::getEvaluationScore)
                    .average();
            if (avgEval.isPresent()) {
                totalEvaluation += avgEval.getAsDouble();
                evaluatedCourses++;
            }

            totalCreditHours += records.stream()
                    .filter(TrainingRecord::isPassed)
                    .mapToDouble(r -> r.getCreditHours() != null ? r.getCreditHours() : 0)
                    .sum();
        }

        int totalEmployees = employeeService.getEmployeesByDepartment(departmentId).size();
        double participationRate = totalEmployees > 0
                ? (double) uniqueParticipants.size() / totalEmployees * 100 : 0;
        double avgAttendanceRate = totalCourses > 0 ? totalAttendanceRate / totalCourses * 100 : 0;
        double avgPassRate = coursesWithExam > 0 ? totalPassRate / coursesWithExam * 100 : 0;
        double avgEvaluation = evaluatedCourses > 0 ? totalEvaluation / evaluatedCourses : 0;

        DepartmentBudget budget = budgetService.getDepartmentBudget(departmentId, Year.of(year));
        BigDecimal totalBudget = budget != null ? budget.getTotalBudget() : BigDecimal.ZERO;
        BigDecimal usedBudget = budget != null ? budget.getUsedBudget() : BigDecimal.ZERO;
        BigDecimal overspentBudget = budget != null ? budget.getOverspentBudget() : BigDecimal.ZERO;
        double budgetUsageRate = totalBudget.compareTo(BigDecimal.ZERO) > 0
                ? usedBudget.divide(totalBudget, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;

        DepartmentStatistics stats = new DepartmentStatistics();
        stats.setDepartmentId(departmentId);
        stats.setDepartmentName(dept.getName());
        stats.setYear(year);
        stats.setQuarter(quarter);
        stats.setTotalCourses(totalCourses);
        stats.setCompletedCourses(completedCourses);
        stats.setOngoingCourses(ongoingCourses);
        stats.setTotalEnrollments(totalEnrollments);
        stats.setUniqueParticipants(uniqueParticipants.size());
        stats.setTotalEmployees(totalEmployees);
        stats.setParticipationRate(Math.round(participationRate * 100) / 100.0);
        stats.setAverageAttendanceRate(Math.round(avgAttendanceRate * 100) / 100.0);
        stats.setAverageExamPassRate(Math.round(avgPassRate * 100) / 100.0);
        stats.setTotalBudget(totalBudget);
        stats.setUsedBudget(usedBudget);
        stats.setOverspentBudget(overspentBudget);
        stats.setBudgetUsageRate(Math.round(budgetUsageRate * 100) / 100.0);
        stats.setAverageEvaluationScore(Math.round(avgEvaluation * 100) / 100.0);
        stats.setTotalCreditHours(Math.round(totalCreditHours * 100) / 100.0);
        stats.setCoursesByType(coursesByType);
        stats.setNewCertificates(newCertificates);

        return stats;
    }

    public CompanyStatistics getCompanyStatistics(int year, int quarter) {
        Collection<Department> departments = departmentService.getAllDepartments();

        List<DepartmentStatistics> deptStatsList = new ArrayList<>();
        for (Department dept : departments) {
            try {
                deptStatsList.add(getDepartmentStatistics(dept.getId(), year, quarter));
            } catch (Exception e) {
            }
        }

        int totalEmployees = (int) employeeService.getAllEmployees().size();
        int totalCourses = deptStatsList.stream().mapToInt(DepartmentStatistics::getTotalCourses).sum();
        int completedCourses = deptStatsList.stream().mapToInt(DepartmentStatistics::getCompletedCourses).sum();
        int totalEnrollments = deptStatsList.stream().mapToInt(DepartmentStatistics::getTotalEnrollments).sum();
        int totalParticipants = deptStatsList.stream().mapToInt(DepartmentStatistics::getUniqueParticipants).sum();
        BigDecimal totalBudget = deptStatsList.stream()
                .map(DepartmentStatistics::getTotalBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalUsedBudget = deptStatsList.stream()
                .map(DepartmentStatistics::getUsedBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOverspent = deptStatsList.stream()
                .map(DepartmentStatistics::getOverspentBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double totalHours = deptStatsList.stream().mapToDouble(DepartmentStatistics::getTotalCreditHours).sum();
        int totalCertificates = deptStatsList.stream().mapToInt(DepartmentStatistics::getNewCertificates).sum();

        double overallParticipationRate = totalEmployees > 0
                ? (double) totalParticipants / totalEmployees * 100 : 0;
        double overallAttendanceRate = !deptStatsList.isEmpty()
                ? deptStatsList.stream().mapToDouble(DepartmentStatistics::getAverageAttendanceRate).average().orElse(0) : 0;
        double overallPassRate = !deptStatsList.isEmpty()
                ? deptStatsList.stream().mapToDouble(DepartmentStatistics::getAverageExamPassRate).average().orElse(0) : 0;

        Map<String, Integer> coursesByType = new HashMap<>();
        for (DepartmentStatistics ds : deptStatsList) {
            if (ds.getCoursesByType() != null) {
                ds.getCoursesByType().forEach((k, v) ->
                        coursesByType.merge(k, v, Integer::sum));
            }
        }

        CompanyStatistics stats = new CompanyStatistics();
        stats.setYear(year);
        stats.setQuarter(quarter);
        stats.setTotalDepartments(departments.size());
        stats.setTotalEmployees(totalEmployees);
        stats.setTotalCourses(totalCourses);
        stats.setCompletedCourses(completedCourses);
        stats.setTotalEnrollments(totalEnrollments);
        stats.setTotalParticipants(totalParticipants);
        stats.setOverallParticipationRate(Math.round(overallParticipationRate * 100) / 100.0);
        stats.setOverallAttendanceRate(Math.round(overallAttendanceRate * 100) / 100.0);
        stats.setOverallPassRate(Math.round(overallPassRate * 100) / 100.0);
        stats.setTotalBudget(totalBudget);
        stats.setTotalUsedBudget(totalUsedBudget);
        stats.setTotalOverspent(totalOverspent);
        stats.setTotalCreditHours(Math.round(totalHours * 100) / 100.0);
        stats.setTotalCertificates(totalCertificates);
        stats.setCoursesByType(coursesByType);
        stats.setDepartmentBreakdown(deptStatsList);

        return stats;
    }

    private List<Course> filterCoursesByPeriod(List<Course> courses, int year, int quarter) {
        return courses.stream()
                .filter(c -> {
                    if (c.getStartTime() == null) return false;
                    int courseYear = c.getStartTime().getYear();
                    int courseQuarter = (c.getStartTime().getMonthValue() - 1) / 3 + 1;
                    return courseYear == year && (quarter == 0 || courseQuarter == quarter);
                })
                .collect(Collectors.toList());
    }
}
