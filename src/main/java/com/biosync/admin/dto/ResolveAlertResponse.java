package com.biosync.admin.dto;

import java.time.Instant;

public record ResolveAlertResponse(Long alertId, boolean resolved, Instant resolvedAt) {
}
