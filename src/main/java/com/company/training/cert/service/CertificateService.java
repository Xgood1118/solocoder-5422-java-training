package com.company.training.cert.service;

import com.company.training.cert.entity.Certificate;
import com.company.training.cert.repository.CertificateRepository;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.record.entity.TrainingRecord;
import com.company.training.record.service.TrainingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseService courseService;
    private final EmployeeService employeeService;
    private final TrainingRecordService recordService;

    public Certificate issueCertificate(String trainingRecordId) {
        TrainingRecord record = recordService.getRecord(trainingRecordId);
        if (record == null) {
            throw new IllegalArgumentException("培训记录不存在: " + trainingRecordId);
        }
        if (!record.isPassed()) {
            throw new IllegalStateException("未通过培训，无法颁发证书");
        }

        Certificate existing = certificateRepository.findByTrainingRecordId(trainingRecordId);
        if (existing != null) {
            return existing;
        }

        Employee employee = employeeService.getEmployee(record.getEmployeeId());
        Course course = courseService.getCourse(record.getCourseId());

        Certificate certificate = new Certificate();
        certificate.setId(UUID.randomUUID().toString());
        certificate.setCertificateNo(generateCertificateNo());
        certificate.setEmployeeId(record.getEmployeeId());
        certificate.setEmployeeName(employee != null ? employee.getName() : "");
        certificate.setCourseId(record.getCourseId());
        certificate.setCourseName(record.getCourseName());
        certificate.setCreditHours(record.getCreditHours());
        certificate.setExamScore(record.getExamScore());
        certificate.setIssuedAt(LocalDateTime.now());
        certificate.setValidUntil(Year.now().plusYears(3).atDay(1).atStartOfDay());
        certificate.setIssuer("公司培训中心");
        certificate.setTrainingRecordId(trainingRecordId);

        return certificateRepository.save(certificate);
    }

    private String generateCertificateNo() {
        String year = String.valueOf(Year.now().getValue());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CERT-" + year + "-" + random;
    }

    public Certificate getCertificate(String id) {
        return certificateRepository.findById(id);
    }

    public Certificate getByCertificateNo(String certificateNo) {
        return certificateRepository.findByCertificateNo(certificateNo);
    }

    public List<Certificate> getCertificatesByEmployee(String employeeId) {
        return certificateRepository.findByEmployeeId(employeeId);
    }

    public List<Certificate> getCertificatesByCourse(String courseId) {
        return certificateRepository.findByCourseId(courseId);
    }

    public Certificate getByTrainingRecord(String trainingRecordId) {
        return certificateRepository.findByTrainingRecordId(trainingRecordId);
    }

    public void deleteCertificate(String id) {
        certificateRepository.deleteById(id);
    }
}
