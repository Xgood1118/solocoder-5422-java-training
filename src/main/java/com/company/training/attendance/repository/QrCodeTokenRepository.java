package com.company.training.attendance.repository;

import com.company.training.attendance.entity.QrCodeToken;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class QrCodeTokenRepository {

    private final ConcurrentHashMap<String, QrCodeToken> storage = new ConcurrentHashMap<>();

    public QrCodeToken save(QrCodeToken token) {
        storage.put(token.getId(), token);
        return token;
    }

    public QrCodeToken findById(String id) {
        return storage.get(id);
    }

    public QrCodeToken findByToken(String token) {
        return storage.values().stream()
                .filter(t -> token.equals(t.getToken()))
                .findFirst()
                .orElse(null);
    }

    public List<QrCodeToken> findValidByCourseId(String courseId) {
        List<QrCodeToken> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (QrCodeToken t : storage.values()) {
            if (courseId.equals(t.getCourseId()) && !t.isUsed() && t.getExpiresAt().isAfter(now)) {
                result.add(t);
            }
        }
        return result;
    }

    public QrCodeToken findLatestValidByCourseId(String courseId) {
        LocalDateTime now = LocalDateTime.now();
        return storage.values().stream()
                .filter(t -> courseId.equals(t.getCourseId()) && !t.isUsed() && t.getExpiresAt().isAfter(now))
                .max((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .orElse(null);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
