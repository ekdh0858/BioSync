package com.biosync.alert.dto;

import com.biosync.alert.domain.AlertSeverity;
import com.biosync.alert.domain.AlertType;
import java.time.Instant;

public record AlertResponse(
        Long alertId,
        AlertType type,
        AlertSeverity severity,
        String message,
        Instant occurredAt,
        boolean resolved
) {
}
