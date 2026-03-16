package com.biosync.summary.service;

import com.biosync.alert.domain.Alert;
import com.biosync.alert.domain.AlertSeverity;
import com.biosync.alert.domain.AlertType;
import com.biosync.alert.repository.AlertRepository;
import com.biosync.biosignal.domain.BioSignal;
import com.biosync.biosignal.service.BioSignalService;
import com.biosync.common.exception.ApiException;
import com.biosync.summary.domain.DailySummary;
import com.biosync.summary.dto.DailySummaryResponse;
import com.biosync.summary.repository.DailySummaryRepository;
import com.biosync.user.domain.User;
import com.biosync.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SummaryService {

    private final DailySummaryRepository dailySummaryRepository;
    private final BioSignalService bioSignalService;
    private final UserRepository userRepository;
    private final AlertRepository alertRepository;

    public DailySummaryResponse getDailySummary(Long userId, LocalDate date) {
        return toResponse(recalculateSummary(userId, date));
    }

    public DailySummary recalculateSummary(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
        List<BioSignal> bioSignals = bioSignalService.findByUserAndDate(userId, date);

        int signalCount = bioSignals.size();
        var heartRates = bioSignals.stream().map(BioSignal::getHeartRate).filter(java.util.Objects::nonNull).toList();
        var stressLevels = bioSignals.stream().map(BioSignal::getStressLevel).filter(java.util.Objects::nonNull).toList();

        int totalSteps = bioSignals.stream()
                .map(BioSignal::getStepCount)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        int totalSleepMinutes = Math.min(1440, bioSignals.stream()
                .map(BioSignal::getSleepMinutes)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum());

        Integer averageHeartRate = heartRates.isEmpty() ? null : (int) Math.round(heartRates.stream().mapToInt(Integer::intValue).average().orElse(0));
        Integer maxHeartRate = heartRates.isEmpty() ? null : heartRates.stream().max(Comparator.naturalOrder()).orElse(null);
        Integer minHeartRate = heartRates.isEmpty() ? null : heartRates.stream().min(Comparator.naturalOrder()).orElse(null);
        Integer averageStressLevel = stressLevels.isEmpty() ? null : (int) Math.round(stressLevels.stream().mapToInt(Integer::intValue).average().orElse(0));

        DailySummary summary = dailySummaryRepository.findByUserIdAndSummaryDate(userId, date)
                .orElseGet(() -> DailySummary.builder()
                        .user(user)
                        .summaryDate(date)
                        .averageHeartRate(averageHeartRate)
                        .maxHeartRate(maxHeartRate)
                        .minHeartRate(minHeartRate)
                        .totalSteps(totalSteps)
                        .totalSleepMinutes(totalSleepMinutes)
                        .averageStressLevel(averageStressLevel)
                        .signalCount(signalCount)
                        .aggregatedAt(Instant.now())
                        .build());

        summary.update(
                averageHeartRate,
                maxHeartRate,
                minHeartRate,
                totalSteps,
                totalSleepMinutes,
                averageStressLevel,
                signalCount,
                Instant.now());

        DailySummary saved = dailySummaryRepository.save(summary);
        createLowActivityAlertIfNeeded(user, saved);
        return saved;
    }

    private void createLowActivityAlertIfNeeded(User user, DailySummary summary) {
        List<DailySummary> previousThree = dailySummaryRepository.findTop3ByUserIdAndSummaryDateBeforeOrderBySummaryDateDesc(
                user.getId(),
                summary.getSummaryDate());

        if (previousThree.size() < 3) {
            return;
        }

        double averageSteps = previousThree.stream().mapToInt(DailySummary::getTotalSteps).average().orElse(0);
        if (averageSteps == 0 || summary.getTotalSteps() > averageSteps * 0.5) {
            return;
        }

        var existing = alertRepository.findTopByUserIdAndTypeAndResolvedFalseOrderByOccurredAtDesc(
                user.getId(),
                AlertType.LOW_ACTIVITY);
        if (existing.isPresent()
                && existing.get().getOccurredAt().atZone(java.time.ZoneId.of("Asia/Seoul")).toLocalDate()
                .equals(summary.getSummaryDate())) {
            return;
        }

        alertRepository.save(Alert.builder()
                .user(user)
                .type(AlertType.LOW_ACTIVITY)
                .severity(AlertSeverity.WARNING)
                .message("Daily activity dropped significantly")
                .occurredAt(summary.getAggregatedAt())
                .resolved(false)
                .build());
    }

    private DailySummaryResponse toResponse(DailySummary summary) {
        return new DailySummaryResponse(
                summary.getSummaryDate(),
                summary.getAverageHeartRate(),
                summary.getMaxHeartRate(),
                summary.getMinHeartRate(),
                summary.getTotalSteps(),
                summary.getTotalSleepMinutes(),
                summary.getAverageStressLevel(),
                summary.getSignalCount());
    }
}
