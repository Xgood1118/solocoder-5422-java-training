package com.company.training.cert.repository;

import com.company.training.cert.entity.Certificate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CertificateRepository {

    private final ConcurrentHashMap<String, Certificate> storage = new ConcurrentHashMap<>();

    public Certificate save(Certificate certificate) {
        storage.put(certificate.getId(), certificate);
        return certificate;
    }

    public Certificate findById(String id) {
        return storage.get(id);
    }

    public Certificate findByCertificateNo(String certificateNo) {
        return storage.values().stream()
                .filter(c -> certificateNo.equals(c.getCertificateNo()))
                .findFirst()
                .orElse(null);
    }

    public List<Certificate> findByEmployeeId(String employeeId) {
        List<Certificate> result = new ArrayList<>();
        for (Certificate c : storage.values()) {
            if (employeeId.equals(c.getEmployeeId())) {
                result.add(c);
            }
        }
        return result;
    }

    public List<Certificate> findByCourseId(String courseId) {
        List<Certificate> result = new ArrayList<>();
        for (Certificate c : storage.values()) {
            if (courseId.equals(c.getCourseId())) {
                result.add(c);
            }
        }
        return result;
    }

    public Certificate findByTrainingRecordId(String trainingRecordId) {
        return storage.values().stream()
                .filter(c -> trainingRecordId.equals(c.getTrainingRecordId()))
                .findFirst()
                .orElse(null);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
