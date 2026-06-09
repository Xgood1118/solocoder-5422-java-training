package com.company.training.common.init;

import com.company.training.common.enums.*;
import com.company.training.course.entity.InternalTechCourse;
import com.company.training.course.service.CourseService;
import com.company.training.department.entity.Department;
import com.company.training.department.service.DepartmentService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final CourseService courseService;

    @PostConstruct
    public void init() {
        if (!departmentService.getAllDepartments().isEmpty()) {
            return;
        }

        Department techDept = new Department();
        techDept.setId("dept-tech");
        techDept.setName("技术部");
        techDept.setHeadCount(200);
        techDept.setAnnualBudget(new BigDecimal("500000"));
        techDept.setBudgetYear(Year.now());
        departmentService.createDepartment(techDept);

        Department productDept = new Department();
        productDept.setId("dept-product");
        productDept.setName("产品部");
        productDept.setHeadCount(50);
        productDept.setAnnualBudget(new BigDecimal("300000"));
        productDept.setBudgetYear(Year.now());
        departmentService.createDepartment(productDept);

        Department salesDept = new Department();
        salesDept.setId("dept-sales");
        salesDept.setName("销售部");
        salesDept.setHeadCount(100);
        salesDept.setAnnualBudget(new BigDecimal("800000"));
        salesDept.setBudgetYear(Year.now());
        departmentService.createDepartment(salesDept);

        Department hrDept = new Department();
        hrDept.setId("dept-hr");
        hrDept.setName("人力资源部");
        hrDept.setHeadCount(20);
        hrDept.setAnnualBudget(new BigDecimal("100000"));
        hrDept.setBudgetYear(Year.now());
        departmentService.createDepartment(hrDept);

        Employee emp1 = new Employee();
        emp1.setId("emp-001");
        emp1.setName("张三");
        emp1.setEmployeeNo("E001");
        emp1.setDepartmentId("dept-tech");
        emp1.setPosition(PositionType.RND);
        emp1.setEmail("zhangsan@company.com");
        emp1.setPhone("13800000001");
        employeeService.createEmployee(emp1);

        Employee emp2 = new Employee();
        emp2.setId("emp-002");
        emp2.setName("李四");
        emp2.setEmployeeNo("E002");
        emp2.setDepartmentId("dept-tech");
        emp2.setPosition(PositionType.RND);
        emp2.setEmail("lisi@company.com");
        emp2.setPhone("13800000002");
        employeeService.createEmployee(emp2);

        Employee emp3 = new Employee();
        emp3.setId("emp-003");
        emp3.setName("王五");
        emp3.setEmployeeNo("E003");
        emp3.setDepartmentId("dept-sales");
        emp3.setPosition(PositionType.SALES);
        emp3.setEmail("wangwu@company.com");
        emp3.setPhone("13800000003");
        employeeService.createEmployee(emp3);

        Employee emp4 = new Employee();
        emp4.setId("emp-004");
        emp4.setName("赵六");
        emp4.setEmployeeNo("E004");
        emp4.setDepartmentId("dept-tech");
        emp4.setPosition(PositionType.MANAGEMENT);
        emp4.setDepartmentManager(true);
        emp4.setEmail("zhaoliu@company.com");
        emp4.setPhone("13800000004");
        employeeService.createEmployee(emp4);

        Employee emp5 = new Employee();
        emp5.setId("emp-005");
        emp5.setName("钱七");
        emp5.setEmployeeNo("E005");
        emp5.setDepartmentId("dept-hr");
        emp5.setPosition(PositionType.MANAGEMENT);
        emp5.setHrManager(true);
        emp5.setEmail("qianqi@company.com");
        emp5.setPhone("13800000005");
        employeeService.createEmployee(emp5);

        InternalTechCourse course1 = new InternalTechCourse();
        course1.setName("React进阶实战");
        course1.setDepartmentId("dept-tech");
        course1.setDescription("深入学习React高级特性和最佳实践");
        course1.setCapacity(30);
        course1.setCreditHours(16.0);
        course1.setStartTime(LocalDateTime.now().plusDays(7));
        course1.setEndTime(LocalDateTime.now().plusDays(7).plusHours(8));
        course1.setSignInMethod(SignInMethod.QR_CODE);
        course1.setCostPerPerson(BigDecimal.ZERO);
        course1.setRequiresExam(true);
        course1.setOrganizerId("emp-004");
        course1.setLecturer("陈架构师");
        course1.setLecturerTitle("首席架构师");
        course1.setLocation("公司培训室A");
        course1.setTechStack("React, TypeScript");
        courseService.createInternalTechCourse(course1);
        courseService.publishCourse(course1.getId());

        InternalTechCourse course2 = new InternalTechCourse();
        course2.setName("Java性能调优");
        course2.setDepartmentId("dept-tech");
        course2.setDescription("JVM调优、数据库优化、缓存策略");
        course2.setCapacity(25);
        course2.setCreditHours(12.0);
        course2.setStartTime(LocalDateTime.now().plusDays(14));
        course2.setEndTime(LocalDateTime.now().plusDays(14).plusHours(6));
        course2.setSignInMethod(SignInMethod.FACE_RECOGNITION);
        course2.setCostPerPerson(BigDecimal.ZERO);
        course2.setRequiresExam(true);
        course2.setOrganizerId("emp-004");
        course2.setLecturer("孙技术专家");
        course2.setLecturerTitle("资深技术专家");
        course2.setLocation("公司培训室B");
        course2.setTechStack("Java, JVM, MySQL");
        courseService.createInternalTechCourse(course2);
        courseService.publishCourse(course2.getId());
    }
}
