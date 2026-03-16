package com.biosync.admin.dto;

import jakarta.validation.constraints.NotNull;

public record ResolveAlertRequest(@NotNull Boolean resolved) {
}
