package com.biosync.biosignal.dto;

import java.time.Instant;

public record BioSignalBatchUploadResponse(
        Long deviceId,
        String uploadRequestId,
        int receivedCount,
        int savedCount,
        int failedCount,
        Instant uploadedAt
) {
}
