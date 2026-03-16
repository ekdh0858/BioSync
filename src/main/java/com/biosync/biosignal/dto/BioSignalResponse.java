package com.biosync.biosignal.dto;

import java.time.Instant;

public record BioSignalResponse(
        Long bioSignalId,
        Long deviceId,
        Instant recordedAt,
        Integer heartRate,
        Integer stepCount,
        Integer sleepMinutes,
        Integer stressLevel
) {
}
