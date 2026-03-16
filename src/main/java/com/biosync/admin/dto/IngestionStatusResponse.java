package com.biosync.admin.dto;

import java.time.LocalDate;

public record IngestionStatusResponse(
        LocalDate date,
        long totalRegisteredUsers,
        long successfulUploadUsers,
        long uploadFailureCount,
        long missingUsers,
        long criticalAlertCount
) {
}
