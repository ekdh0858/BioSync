package com.biosync.summary.dto;

import java.time.LocalDate;

public record DailySummaryResponse(
        LocalDate date,
        Integer averageHeartRate,
        Integer maxHeartRate,
        Integer minHeartRate,
        Integer totalSteps,
        Integer totalSleepMinutes,
        Integer averageStressLevel,
        Integer signalCount
) {
}
