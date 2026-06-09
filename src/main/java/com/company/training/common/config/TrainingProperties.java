package com.company.training.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "training")
public class TrainingProperties {

    private GpsConfig companyGps = new GpsConfig();
    private ExamConfig exam = new ExamConfig();
    private AnnualHoursConfig annualHours = new AnnualHoursConfig();
    private QrCodeConfig qrCode = new QrCodeConfig();

    @Data
    public static class GpsConfig {
        private double latitude;
        private double longitude;
        private double radiusMeters;
    }

    @Data
    public static class ExamConfig {
        private int passScore = 80;
        private int maxRetries = 1;
    }

    @Data
    public static class AnnualHoursConfig {
        private int management = 40;
        private int rnd = 60;
        private int sales = 80;
        private int minExternalHours = 20;
    }

    @Data
    public static class QrCodeConfig {
        private int validMinutes = 5;
    }
}
