package com.biosync.admin.dto;

import com.biosync.alert.dto.AlertResponse;
import com.biosync.device.dto.DeviceResponse;
import com.biosync.user.domain.Role;
import com.biosync.user.domain.UserStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AdminUserDetailResponse(
        Long userId,
        String email,
        String name,
        LocalDate birthDate,
        Role role,
        UserStatus status,
        Instant lastLoginAt,
        Instant latestUploadAt,
        List<DeviceResponse> devices,
        List<AlertResponse> recentAlerts
) {
}
