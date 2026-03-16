package com.biosync.biosignal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record BioSignalItemRequest(
        @NotNull Instant recordedAt,
        @Min(20) @Max(240) Integer heartRate,
        @Min(0) @Max(100000) Integer stepCount,
        @Min(0) @Max(1440) Integer sleepMinutes,
        @Min(0) @Max(100) Integer stressLevel
) {
    public boolean hasMetric() {
        return heartRate != null || stepCount != null || sleepMinutes != null || stressLevel != null;
    }
}
