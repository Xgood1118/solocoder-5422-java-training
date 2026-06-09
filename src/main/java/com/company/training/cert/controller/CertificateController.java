package com.company.training.cert.controller;

import com.company.training.cert.entity.Certificate;
import com.company.training.cert.service.CertificateService;
import com.company.training.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/issue")
    public ApiResponse<Certificate> issue(@RequestBody Map<String, String> request) {
        try {
            String trainingRecordId = request.get("trainingRecordId");
            return ApiResponse.success(certificateService.issueCertificate(trainingRecordId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Certificate> getById(@PathVariable String id) {
        Certificate cert = certificateService.getCertificate(id);
        if (cert == null) {
            return ApiResponse.error(404, "证书不存在");
        }
        return ApiResponse.success(cert);
    }

    @GetMapping("/by-no/{certificateNo}")
    public ApiResponse<Certificate> getByNo(@PathVariable String certificateNo) {
        Certificate cert = certificateService.getByCertificateNo(certificateNo);
        if (cert == null) {
            return ApiResponse.error(404, "证书不存在");
        }
        return ApiResponse.success(cert);
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<Certificate>> getByEmployee(@PathVariable String employeeId) {
        return ApiResponse.success(certificateService.getCertificatesByEmployee(employeeId));
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<Certificate>> getByCourse(@PathVariable String courseId) {
        return ApiResponse.success(certificateService.getCertificatesByCourse(courseId));
    }

    @GetMapping("/by-training-record/{trainingRecordId}")
    public ApiResponse<Certificate> getByTrainingRecord(@PathVariable String trainingRecordId) {
        Certificate cert = certificateService.getByTrainingRecord(trainingRecordId);
        if (cert == null) {
            return ApiResponse.error(404, "证书不存在");
        }
        return ApiResponse.success(cert);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        certificateService.deleteCertificate(id);
        return ApiResponse.success();
    }
}
