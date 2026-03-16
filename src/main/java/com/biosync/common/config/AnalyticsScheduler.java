package com.biosync.common.config;

import com.biosync.alert.service.AlertService;
import com.biosync.summary.service.SummaryService;
import com.biosync.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsScheduler {

    private final UserRepository userRepository;
    private final SummaryService summaryService;
    private final AlertService alertService;

    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    public void recalculatePreviousDaySummaries() {
        LocalDate targetDate = LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).minusDays(1);
        userRepository.findAll().forEach(user -> summaryService.recalculateSummary(user.getId(), targetDate));
    }

    @Scheduled(cron = "0 30 23 * * *", zone = "Asia/Seoul")
    public void createUploadMissingAlerts() {
        LocalDate targetDate = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        alertService.createUploadMissingAlerts(targetDate);
    }
}
